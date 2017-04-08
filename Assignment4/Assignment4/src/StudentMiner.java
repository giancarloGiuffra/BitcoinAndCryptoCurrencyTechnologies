import java.util.HashSet;
import java.util.Set;

public class StudentMiner extends Miner {

    private Set<Simulation.Transaction> transactions;

    public StudentMiner(Block genesisBlock) {
        super(genesisBlock);
        transactions = new HashSet<>();
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
    }

    @Override
    public Block findBlock() {
        long newId = Simulation.getNewId(this);
        Block fakeBlock = new Block(0, null, new HashSet<>());
        Block foundBlock = new Block(newId, fakeBlock, transactions);
        transactions.clear();
        return foundBlock;
    }
}