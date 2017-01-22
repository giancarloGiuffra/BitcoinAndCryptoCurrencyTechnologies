import java.util.*;
import java.util.stream.Collectors;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private boolean[] followees;
    private Set<Transaction> consensusTransactions;
    private Map<Transaction, Integer> observedTransactions;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.consensusTransactions = new HashSet<Transaction>(){};
        this.observedTransactions = new HashMap<Transaction,Integer>();
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        consensusTransactions = pendingTransactions;
        UpdateObservedTxCounterFor((pendingTransactions.stream().collect(Collectors.toList())));
    }

    private void UpdateObservedTxCounterFor(List<Transaction> transactions) {
        transactions.forEach(tx -> observedTransactions.put(tx, observedTransactions.getOrDefault(tx, 0) + 1));
    }

    public Set<Transaction> getProposals() {
        return consensusTransactions;
    }

    public void receiveCandidates(ArrayList<Integer[]> candidates) {
        List<Transaction> transactions = candidates.stream()
                                        .mapToInt(candidate -> candidate[0])
                                        .mapToObj(id -> new Transaction(id))
                                        .collect(Collectors.toList());

        UpdateObservedTxCounterFor(transactions);
        UpdateConsensusTransactions();
    }

    private void UpdateConsensusTransactions() {
        Set<Transaction> transactionsSeenAtLeastTwice = GetTransactionsSeenAtLeast(2);
        consensusTransactions.addAll(transactionsSeenAtLeastTwice);
    }

    private Set<Transaction> GetTransactionsSeenAtLeast(int n) {
        Set<Transaction> transactionsSeenAtLeastTwice = observedTransactions.entrySet().stream()
                                                        .filter(entry -> entry.getValue() >= n)
                                                        .map(entry -> entry.getKey())
                                                        .collect(Collectors.toSet());

        transactionsSeenAtLeastTwice.forEach(tx -> observedTransactions.remove(tx));
        return transactionsSeenAtLeastTwice;
    }

    public Set<Transaction> sendToFollowers() {
        return consensusTransactions;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        List<Transaction> transactions = candidates.stream()
                                        .map(candidate -> candidate.tx)
                                        .distinct()
                                        .collect(Collectors.toList());
        UpdateObservedTxCounterFor(transactions);
        UpdateConsensusTransactions();
    }
}
