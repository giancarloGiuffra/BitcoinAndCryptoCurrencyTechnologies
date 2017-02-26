// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockChain {
    public static int CUT_OFF_AGE = 10;
    private BlockChainNode highestNode;
    private Map<byte[], BlockChainNode> chain;
    private TransactionPool transactionPool;

    /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    public BlockChain(Block genesisBlock) {
        UTXOPool genesisUTXOPool = UTXOPoolPlusCoinBaseUTXO(new UTXOPool(), genesisBlock);
        BlockChainNode genesisNode = new BlockChainNode(genesisBlock, genesisUTXOPool, 0);
        this.chain = new HashMap<byte[], BlockChainNode>(){{ put(genesisBlock.getHash(), genesisNode); }};
        this.highestNode = genesisNode;
        this.transactionPool = new TransactionPool();
    }

    private UTXOPool UTXOPoolPlusCoinBaseUTXO(UTXOPool utxoPool, Block block) {
        Transaction coinBaseTx = block.getCoinbase();
        utxoPool.addUTXO(new UTXO(coinBaseTx.getHash(), 0), coinBaseTx.getOutput(0));
        return utxoPool;
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        return highestNode.getBlock();
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() { return highestNode.getUtxoPool(); }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
        return  new TransactionPool(transactionPool);
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * 
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {

        if(NotFakeGenesis(block) && AllTransactionsAreValid(block) && HeightConditionIsVerified(block)){
           ProcessBlock(block);
           return true;
        }

        return false;
    }

    private void ProcessBlock(Block block) {
        BlockChainNode node = BuildBlockChainNode(block);
        chain.put(block.getHash(), node);
        UpdateHighestNode(node);
        RemoveTransactionsFromPool(block.getTransactions());
    }

    private void RemoveTransactionsFromPool(List<Transaction> txs) {
        txs.forEach(tx -> transactionPool.removeTransaction(tx.getHash()));
    }

    private void UpdateHighestNode(BlockChainNode node) {
        if(highestNode.getHeight() < node.getHeight()){
            highestNode = node;
        }
    }

    private BlockChainNode BuildBlockChainNode(Block block) {
        BlockChainNode parentNode = chain.get(block.getPrevBlockHash());
        TxHandler txHandler = new TxHandler(parentNode.getUtxoPool());
        txHandler.handleTxs(block.getTransactions());

        return new BlockChainNode(block, UTXOPoolPlusCoinBaseUTXO(txHandler.getUTXOPool(), block), parentNode.getHeight() + 1);
    }

    private boolean HeightConditionIsVerified(Block block) {
        BlockChainNode parentNode = chain.get(block.getPrevBlockHash());
        return parentNode.getHeight() + 1 > highestNode.getHeight() - CUT_OFF_AGE;
    }

    private boolean NotFakeGenesis(Block block) {
        return block.getHash() != null;
    }

    private boolean AllTransactionsAreValid(Block block) {
        BlockChainNode parentNode = chain.get(block.getPrevBlockHash());
        List<Transaction> validTransactions = new TxHandler(parentNode.getUtxoPool()).handleTxs(block.getTransactions());
        return validTransactions.size() == block.getTransactions().size();
    }

    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {
        transactionPool.addTransaction(tx);
    }
}