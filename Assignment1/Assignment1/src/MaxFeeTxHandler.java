import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class MaxFeeTxHandler extends TxHandler {

    public MaxFeeTxHandler(UTXOPool utxoPool) {
        super(utxoPool);
    }

    @Override
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        return super.handleTxs(SortedByFeesInDecreasingOrder(possibleTxs));
    }

    private Transaction[] SortedByFeesInDecreasingOrder(Transaction[] possibleTxs) {
        return ToArray(Arrays.asList(possibleTxs).stream()
                             .sorted(HighFeesFirst())
                             .collect(Collectors.toList()));
    }

    private Comparator<Transaction> HighFeesFirst() {
        return (tx1, tx2) -> {
            boolean tx1FeesCanBeCalculated = AllUTXOsRequiredByTransactionAreInPool(tx1);
            boolean tx2FeesCanBeCalculated = AllUTXOsRequiredByTransactionAreInPool(tx2);
            if (!tx1FeesCanBeCalculated && !tx2FeesCanBeCalculated) return 0;
            if (!tx1FeesCanBeCalculated) return 1;
            if (!tx2FeesCanBeCalculated) return -1;
            return Fees(tx2) - Fees(tx1) < 0 ? -1 : 1;
        };
    }

    private double Fees(Transaction tx) {
        double totalClaimedUTXOs = UTXOsFromInputs(tx).mapToDouble(utxo -> utxoPool.getTxOutput(utxo).value).sum();
        double totalOutputs = tx.getOutputs().stream().mapToDouble(output -> output.value).sum();
        return totalClaimedUTXOs - totalOutputs;
    }
}
