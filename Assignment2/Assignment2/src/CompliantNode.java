import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private boolean[] followees;
    private Set<Transaction> observedTransactions;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.observedTransactions = new HashSet<Transaction>(){};
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.observedTransactions = pendingTransactions;
    }

    @Override
    public Set<Transaction> getProposals() {
        return observedTransactions;
    }

    @Override
    public void receiveCandidates(ArrayList<Integer[]> candidates) {
        Set<Transaction> transactions = candidates.stream()
                .mapToInt(candidate -> candidate[0])
                .mapToObj(id -> new Transaction(id))
                .collect(Collectors.toSet());
        observedTransactions.addAll(transactions);
    }

    public Set<Transaction> sendToFollowers() {
        return observedTransactions;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        Stream<Transaction> stream = candidates.stream().map(candidate -> candidate.tx).distinct();
        observedTransactions.addAll(stream.collect(Collectors.toSet()));
    }
}
