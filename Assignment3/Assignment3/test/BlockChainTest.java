import org.junit.Test;

import java.security.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BlockChainTest {

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

    private Transaction TransactionSpendingAllCoinBase(Block block, KeyPair blockMiner, KeyPair receiver) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Transaction tx = new Transaction();
        tx.addInput(block.getCoinbase().getHash(), 0);
        Signature signature = SignatureForSingleInputTx(tx, blockMiner);
        tx.addSignature(signature.sign(), 0);
        tx.addOutput(25, receiver.getPublic());
        return tx;
    }

    private Signature SignatureForSingleInputTx(Transaction singleInputTx, KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(singleInputTx.getRawDataToSign(0));
        return signature;
    }

    private KeyPair KeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024, SecureRandom.getInstance("SHA1PRNG"));
        return generator.generateKeyPair();
    }

}