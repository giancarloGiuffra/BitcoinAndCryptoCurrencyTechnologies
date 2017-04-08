import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class StudentMinerTest {


    private Block genesisBlock = new Block(0, null, new HashSet<>());

    @Before
    public void SetUp(){
        Simulation.txOwnerIds.add((long) new Random().nextInt());
        Simulation.ownerIds = new HashMap<Miner, Set<Long>>();
    }

    @Test
    public void ShouldStartWithNoTransactionsHeard() {
        StudentMiner miner = Miner();

        assertTrue(miner.getTransactions().isEmpty());
    }

    @Test
    public void ShouldSaveHeardTransactions() throws Exception {
        StudentMiner miner = Miner();
        Simulation.Transaction tx = new Simulation.Transaction();
        miner.hearTransaction(tx);

        Set<Simulation.Transaction> heardTransactions = miner.getTransactions();
        assertThat(heardTransactions.size(), equalTo(1));
        assertThat(heardTransactions, hasItem(tx));
    }

    @Test
    public void ShouldRemoveTransactionsInBlockFromHeardTransactions() throws Exception {
        StudentMiner miner = Miner();
        Simulation.Transaction txInFutureBlock = new Simulation.Transaction();
        Simulation.Transaction txNotInFutureBlock = new Simulation.Transaction();
        miner.hearTransaction(txInFutureBlock);
        miner.hearTransaction(txNotInFutureBlock);

        Block block = new Block(0, genesisBlock, new HashSet<>(Arrays.asList(txInFutureBlock)));
        miner.hearBlock(block);

        Set<Simulation.Transaction> heardTransactions = miner.getTransactions();
        assertThat(heardTransactions.size(), equalTo(1));
        assertThat(heardTransactions, hasItem(txNotInFutureBlock));
    }

    @Test
    public void ShouldReturnHeardTransactionsInFoundBlock() throws Exception {
        StudentMiner miner = Miner();
        Simulation.Transaction tx1 = new Simulation.Transaction();
        Simulation.Transaction tx2 = new Simulation.Transaction();
        miner.hearTransaction(tx1);
        miner.hearTransaction(tx2);

        Block foundBlock = miner.findBlock();
        assertThat(foundBlock.transactions, hasItems(tx1, tx2));
    }

    private StudentMiner Miner() {
        return new StudentMiner(genesisBlock);
    }
}
