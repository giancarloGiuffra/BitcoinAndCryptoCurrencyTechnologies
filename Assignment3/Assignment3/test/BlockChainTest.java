import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BlockChainTest {

    @Before
    @After
    public void setUp() throws Exception {
        BlockChain.CUT_OFF_AGE = 10;
    }

    @Test
    public void ShouldCreateABlockChainWithOnlyAGenesisBlock() throws Exception {
        KeyPair pair = KeyPair();
        Block genesisBlock = new Block(null, pair.getPublic());
        BlockChain blockChain = new BlockChain(genesisBlock);

        Block maxHeightBlock = blockChain.getMaxHeightBlock();
        Transaction coinBaseTx = new Transaction(25, pair.getPublic());

        assertThat(maxHeightBlock.getHash(), equalTo(null));
        assertThat(maxHeightBlock.getCoinbase(), equalTo(coinBaseTx));
        assertThat(blockChain.getMaxHeightUTXOPool().getAllUTXO().size(), equalTo(1));
        assertThat(blockChain.getMaxHeightUTXOPool().getAllUTXO().get(0), equalTo(new UTXO(coinBaseTx.getHash(), 0)));
    }

    @Test
    public void ShouldAddValidBlockOverGenesisBlock() throws Exception {
        KeyPair genesisPair = KeyPair();
        Block genesisBlock = new Block(null, genesisPair.getPublic());
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);

        KeyPair keyPair = KeyPair();
        Block validBlock = new Block(genesisBlock.getHash(), keyPair.getPublic());
        Transaction txSpendingGenesisCoinBase = TransactionSpendingAllCoinBase(genesisBlock, genesisPair, keyPair);
        validBlock.addTransaction(txSpendingGenesisCoinBase);
        validBlock.finalize();

        assertThat(blockChain.addBlock(validBlock), equalTo(true));
        assertThat(blockChain.getMaxHeightBlock().getHash(), equalTo(validBlock.getHash()));
        assertThat(blockChain.getMaxHeightBlock().getCoinbase(), equalTo(new Transaction(25, keyPair.getPublic())));
    }

    @Test
    public void ShouldBeAbleToSpendCoinBaseTransactionInTheNextBlock() throws Exception {
        KeyPair genesisPair = KeyPair();
        Block genesisBlock = new Block(null, genesisPair.getPublic());
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);

        KeyPair firstKeyPair = KeyPair();
        Block firstBlock = new Block(genesisBlock.getHash(), firstKeyPair.getPublic());
        firstBlock.finalize();
        assertThat(blockChain.addBlock(firstBlock), equalTo(true));

        KeyPair secondKeyPair = KeyPair();
        Block secondBlock = new Block(firstBlock.getHash(), secondKeyPair.getPublic());
        Transaction txSpendingPreviousCoinBase = TransactionSpendingAllCoinBase(firstBlock, firstKeyPair, secondKeyPair);
        secondBlock.addTransaction(txSpendingPreviousCoinBase);
        secondBlock.finalize();

        assertThat(blockChain.addBlock(secondBlock), equalTo(true));
        assertThat(blockChain.getMaxHeightBlock().getHash(), equalTo(secondBlock.getHash()));
        assertThat(blockChain.getMaxHeightBlock().getCoinbase(), equalTo(new Transaction(25, secondKeyPair.getPublic())));
    }

    @Test
    public void ShouldNotBeValidToAddBlockThatViolatesTheHeightConstraint() throws Exception {

        BlockChain.CUT_OFF_AGE = 1;

        KeyPair genesisPair = KeyPair();
        Block genesisBlock = new Block(null, genesisPair.getPublic());
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);

        KeyPair firstKeyPair = KeyPair();
        Block firstBlock = new Block(genesisBlock.getHash(), firstKeyPair.getPublic());
        firstBlock.finalize();
        assertThat(blockChain.addBlock(firstBlock), equalTo(true));

        KeyPair secondKeyPair = KeyPair();
        Block secondBlock = new Block(firstBlock.getHash(), secondKeyPair.getPublic());
        secondBlock.finalize();
        assertThat(blockChain.addBlock(secondBlock), equalTo(true));

        Block validHeightBlock = new Block(firstBlock.getHash(), KeyPair().getPublic());
        validHeightBlock.finalize();
        assertThat(blockChain.addBlock(validHeightBlock), equalTo(true));

        Block invalidHeightBlock = new Block(genesisBlock.getHash(), KeyPair().getPublic());
        invalidHeightBlock.finalize();
        assertThat(blockChain.addBlock(invalidHeightBlock), equalTo(false));
    }

    @Test
    public void ShouldAddTransactionToPool() throws Exception {
        KeyPair genesisPair = KeyPair();
        Block genesisBlock = new Block(null, genesisPair.getPublic());
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);

        Transaction tx = TransactionSpendingAllCoinBase(genesisBlock, genesisPair, KeyPair());
        blockChain.addTransaction(tx);

        assertThat(blockChain.getTransactionPool().getTransactions(), equalTo(new ArrayList<>(Arrays.asList(tx))));
    }

    @Test
    public void ShouldEliminateTxFromPoolIfIncludedInSomeBlock() throws Exception {
        KeyPair genesisPair = KeyPair();
        Block genesisBlock = new Block(null, genesisPair.getPublic());
        genesisBlock.finalize();
        BlockChain blockChain = new BlockChain(genesisBlock);

        Transaction tx = TransactionSpendingAllCoinBase(genesisBlock, genesisPair, KeyPair());
        blockChain.addTransaction(tx);

        KeyPair miner = KeyPair();
        Block block = new Block(genesisBlock.getHash(), miner.getPublic());
        block.addTransaction(tx);
        block.finalize();
        assertThat(blockChain.addBlock(block), equalTo(true));

        assertThat(blockChain.getTransactionPool().getTransactions().isEmpty(), equalTo(true));
    }

    private Transaction TransactionSpendingAllCoinBase(Block block, KeyPair blockMiner, KeyPair receiver) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Transaction tx = new Transaction();
        tx.addInput(block.getCoinbase().getHash(), 0);
        tx.addOutput(25, receiver.getPublic());
        Signature signature = SignatureForSingleInputTx(tx, blockMiner);
        tx.addSignature(signature.sign(), 0);
        tx.finalize();
        return tx;
    }

    private Signature SignatureForSingleInputTx(Transaction singleInputTx, KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(singleInputTx.getRawDataToSign(0));
        return signature;
    }

    private KeyPair KeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        return generator.generateKeyPair();
    }

}