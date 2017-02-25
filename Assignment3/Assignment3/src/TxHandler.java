import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class TxHandler {

	protected UTXOPool utxoPool;

	/* Creates a public ledger whose current UTXOPool (collection of unspent
	 * transaction outputs) is utxoPool. This should make a defensive copy of
	 * utxoPool by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public TxHandler(UTXOPool utxoPool) {
		this.utxoPool = new UTXOPool(utxoPool);
	}

	/* Returns true if 
	 * (1) all outputs claimed by tx are in the current UTXO pool, 
	 * (2) the signatures on each input of tx are valid, 
	 * (3) no UTXO is claimed multiple times by tx, 
	 * (4) all of tx’s output values are non-negative, and
	 * (5) the sum of tx’s input values is greater than or equal to the sum of   
	        its output values;
	   and false otherwise.
	 */

	public boolean isValidTx(Transaction tx) {

        return  AllUTXOsRequiredByTransactionAreInPool(tx) &&
                AllSignaturesAreValid(tx) &&
                AllUTXOsAreClaimedOnlyOnce(tx) &&
                AllTxOutputsHaveNonNegativeValues(tx) &&
                TotalClaimedUTXOsAreAtLeastEqualToOutputs(tx);
	}

    private boolean TotalClaimedUTXOsAreAtLeastEqualToOutputs(Transaction tx) {
        double totalClaimedUTXOs = UTXOsFromInputs(tx).mapToDouble(utxo -> utxoPool.getTxOutput(utxo).value).sum();
        double totalOutputs = tx.getOutputs().stream().mapToDouble(output -> output.value).sum();
        return totalClaimedUTXOs >= totalOutputs;
    }

    private boolean AllTxOutputsHaveNonNegativeValues(Transaction tx) {
        return tx.getOutputs().stream().allMatch(output -> output.value >= 0);
    }

    private boolean AllUTXOsAreClaimedOnlyOnce(Transaction tx) {
        List<UTXO> claimedUtxos = UTXOsFromInputs(tx).collect(toList());
        return claimedUtxos.size() == new HashSet<>(claimedUtxos).size();
    }

    private boolean AllSignaturesAreValid(Transaction tx) {
        ArrayList<Transaction.Input> txInputs = tx.getInputs();
        return !(IntStream.range(0, txInputs.size())
                .mapToObj(index -> {
                    Transaction.Input txInput = txInputs.get(index);
                    Transaction.Output txOutput = utxoPool.getTxOutput(new UTXO(txInput.prevTxHash, txInput.outputIndex));
                    return txInput.signature != null && Crypto.verifySignature(txOutput.address, tx.getRawDataToSign(index), txInput.signature);
                }).collect(toList())
                .contains(false));
    }

    protected boolean AllUTXOsRequiredByTransactionAreInPool(Transaction tx) {
        ArrayList<UTXO> utxosInPool = utxoPool.getAllUTXO();
        return UTXOsFromInputs(tx).allMatch(utxosInPool::contains);
    }

    /* Handles each epoch by receiving an unordered array of proposed
     * transactions, checking each transaction for correctness,
     * returning a mutually valid array of accepted transactions,
     * and updating the current UTXO pool as appropriate.
     */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
        ArrayList<Transaction> acceptedTransactions = new ArrayList<>();
        ArrayList<Transaction> notAcceptedTransactions = new ArrayList<>();
        for (Transaction tx : possibleTxs) {
            if (isValidTx(tx)){
                UpdateUTXOPool(tx);
                acceptedTransactions.add(tx);
            }else{
                notAcceptedTransactions.add(tx);
            }
        }

        if(!acceptedTransactions.isEmpty()){
            acceptedTransactions.addAll(handleTxs(notAcceptedTransactions));
        }

        return ToArray(acceptedTransactions);
	}

    private void UpdateUTXOPool(Transaction tx) {
        UTXOsFromInputs(tx).forEach(utxo -> utxoPool.removeUTXO(utxo));
        IntStream.range(0, tx.getOutputs().size())
                .forEach(index -> utxoPool.addUTXO(new UTXO(tx.getHash(), index), tx.getOutputs().get(index)));
    }

    public List<Transaction> handleTxs(List<Transaction> transactions) {
        return Arrays.asList(handleTxs(ToArray(transactions)));
    }

    protected Transaction[] ToArray(List<Transaction> transactions) {
        return transactions.toArray(new Transaction[transactions.size()]);
    }

    protected Stream<UTXO> UTXOsFromInputs(Transaction tx) {
        return tx.getInputs().stream().map(input -> new UTXO(input.prevTxHash, input.outputIndex));
    }

    public UTXOPool getUTXOPool() {
		return utxoPool;
	}
} 
