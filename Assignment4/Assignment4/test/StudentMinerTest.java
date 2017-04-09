import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
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
        assertTrue(miner.getTransactions().isEmpty());
    }

    @Test
    public void ShouldUseNewIdForNewBlockFound() throws Exception {
        StudentMiner miner = Miner();

        Simulation.Transaction txInFirstFoundBlock = new Simulation.Transaction();
        miner.hearTransaction(txInFirstFoundBlock);
        Block firstFoundBlock = miner.findBlock();

        Simulation.Transaction txInSecondFoundBlock = new Simulation.Transaction();
        miner.hearTransaction(txInSecondFoundBlock);
        Block secondFoundBlock = miner.findBlock();

        assertThat(secondFoundBlock.ownerId, not(equalTo(firstFoundBlock.ownerId)));
    }

    @Test
    public void ShouldMineOverHighestBlock() throws Exception {
        StudentMiner miner = Miner();

        Block block = new Block(0, genesisBlock, new HashSet<>());
        miner.hearBlock(block);

        Simulation.Transaction tx = new Simulation.Transaction();
        miner.hearTransaction(tx);
        Block foundBlock = miner.findBlock();

        assertThat(foundBlock.parent, equalTo(block));
    }

    @Test
    public void ShouldMineOverHighestBlockEvenIfIMinedIt() throws Exception {
        StudentMiner miner = Miner();
        Block firstFoundBlock = miner.findBlock();
        Block secondFoundBlock = miner.findBlock();

        assertThat(secondFoundBlock.parent, equalTo(firstFoundBlock));
    }

    @Test
    public void ShouldPublishMinedBlocksInCorrectOrder() throws Exception {
        StudentMiner miner = Miner();
        Block firstFoundBlock = miner.findBlock();
        Block secondFoundBlock = miner.findBlock();

        ArrayList<Block> blocksToPublish = miner.publishBlock();
        assertThat(blocksToPublish.size(), equalTo(2));
        assertThat(blocksToPublish.get(0), equalTo(firstFoundBlock));
        assertThat(blocksToPublish.get(1), equalTo(secondFoundBlock));
    }

    @Test
    public void ShouldNotPublishFoundBlockMoreThanOnce() throws Exception {
        StudentMiner miner = Miner();
        Block firstFoundBlock = miner.findBlock();

        ArrayList<Block> firstBlocksToPublish = miner.publishBlock();
        assertThat(firstBlocksToPublish.size(), equalTo(1));
        assertThat(firstBlocksToPublish.get(0), equalTo(firstFoundBlock));

        ArrayList<Block> secondBlocksToPublish = miner.publishBlock();
        assertTrue(secondBlocksToPublish.isEmpty());
    }

    private StudentMiner Miner() {
        return new StudentMiner(genesisBlock);
    }
}
