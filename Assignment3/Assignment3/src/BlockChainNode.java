/**
 * Created by gianca on 15/2/17.
 */
public class BlockChainNode {

    private final Block block;
    private final int height;
    private final UTXOPool utxoPool;

    public BlockChainNode(Block block, UTXOPool utxoPool, int height) {
        this.block = block;
        this.utxoPool = utxoPool;
        this.height = height;
    }

    public Block getBlock() {
        return block;
    }

    public int getHeight() { return height; }

    public UTXOPool getUtxoPool() { return utxoPool; }
}
