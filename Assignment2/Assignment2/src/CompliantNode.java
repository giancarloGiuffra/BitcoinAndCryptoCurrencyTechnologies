import java.util.*;
import java.util.stream.Collectors;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private boolean[] followees;
    private Set<Transaction> consensusTransactions;
    private Map<Transaction, Integer> observedTransactions;
    public Map<Transaction, Set<Integer>> transactionSenders;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.consensusTransactions = new HashSet<Transaction>(){};
        this.observedTransactions = new HashMap<Transaction,Integer>();
        this.transactionSenders = new HashMap<Transaction, Set<Integer>>();
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
        UpdateTxObservedSenders(candidates);
        UpdateObservedTxCounterFor(transactions);
        UpdateConsensusTransactions();
    }

    private void UpdateTxObservedSenders(ArrayList<Integer[]> candidates) {
        candidates.forEach(candidate -> {
            Transaction tx = new Transaction(candidate[0]);
            Set<Integer> senderSet = transactionSenders.getOrDefault(tx, new HashSet<Integer>());
            senderSet.add(candidate[1]);
            transactionSenders.put(tx, senderSet);
        });
    }

    private void UpdateConsensusTransactions() {
        Set<Transaction> transactionsSeenFromDifferentSenders = GetTransactionsSeenFromDifferentSenders(2);
        consensusTransactions.addAll(transactionsSeenFromDifferentSenders);
    }

    private Set<Transaction> GetTransactionsSeenFromDifferentSenders(int n) {
        Set<Transaction> transactionsSeenFromDifferentSenders = transactionSenders.entrySet().stream()
                                                                .filter(entry -> entry.getValue().size() >= n)
                                                                .map(entry -> entry.getKey())
                                                                .collect(Collectors.toSet());
        transactionsSeenFromDifferentSenders.forEach(tx -> transactionSenders.remove(tx));
        return transactionsSeenFromDifferentSenders;
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
