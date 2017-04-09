import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StudentMiner extends Miner {

    private Block heighestBlock;
    private Set<Simulation.Transaction> transactions;
    private ArrayList<Block> blocksToPublish;

    public StudentMiner(Block genesisBlock) {
        super(genesisBlock);
        heighestBlock = genesisBlock;
        transactions = new HashSet<>();
        blocksToPublish = new ArrayList<>();
   }

    public Set<Simulation.Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public void hearTransaction(Simulation.Transaction tx) {
        transactions.add(tx);
    }

    @Override
    public void hearBlock(Block block) {
        transactions.removeAll(block.transactions);
        if(block.height > heighestBlock.height){
            heighestBlock = block;
        }
    }

    @Override
    public Block findBlock() {
        long newId = Simulation.getNewId(this);
        Block foundBlock = new Block(newId, heighestBlock, new HashSet<>(transactions));
        transactions.clear();
        heighestBlock = foundBlock;
        blocksToPublish.add(foundBlock);
        return foundBlock;
    }

    @Override
    public ArrayList<Block> publishBlock() {
        ArrayList<Block> blocks = new ArrayList<>(blocksToPublish);
        blocksToPublish.clear();
        return blocks;
    }
}