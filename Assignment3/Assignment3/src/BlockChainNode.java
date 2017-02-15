/**
 * Created by gianca on 15/2/17.
 */
public class BlockChainNode {

    private final Block block;

    public BlockChainNode(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
