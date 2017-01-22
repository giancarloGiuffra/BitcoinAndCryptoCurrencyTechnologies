import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CompliantNodeTest {

    @Test
    public void ShouldConsiderConsensusIfTransactionReceivedFromTwoDifferentSenders(){
        CompliantNode node = new CompliantNode(0, 0, 0, 0);
        node.receiveCandidates(Candidates(Candidate(0, 1), Candidate(0, 2)));

        Transaction tx = new Transaction(0);
        Set<Transaction> consensus = node.getProposals();
        assertThat(consensus.size(), equalTo(1));
        assertThat(consensus.toArray()[0], equalTo(tx));
        assertThat(node.transactionSenders.isEmpty(), equalTo(true));
        assertThat(node.transactionSenders.containsKey(tx), equalTo(false));
    }

    @Test
    public void ShouldKeepTransactionsWithOnlyOneSender(){
        CompliantNode node = new CompliantNode(0, 0, 0, 0);
        node.receiveCandidates(Candidates(
                Candidate(0, 1),
                Candidate(0, 2),
                Candidate(1,1)));

        Transaction tx = new Transaction(0);
        Set<Transaction> consensus = node.getProposals();
        assertThat(consensus.size(), equalTo(1));
        assertThat(consensus.toArray()[0], equalTo(tx));
        assertThat(node.transactionSenders.size(), equalTo(1));
        Set<Integer> sendersOfTxWithId1 = node.transactionSenders.get(new Transaction(1));
        assertThat(sendersOfTxWithId1.size(), equalTo(1));
        assertThat(sendersOfTxWithId1.toArray()[0], equalTo(1));
    }

    private Integer[] Candidate(int transaction, int sender) {
        return new Integer[]{transaction, sender};
    }

    private ArrayList<Integer[]> Candidates(Integer[]... candidates) {
        return new ArrayList<Integer[]>(Arrays.asList(candidates));
    }

}