import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class MaxFeeTxHandlerTest extends TxHandlerTest {

    @Test
    public void ShouldChooseTheTransactionWithHighestFees() throws Exception {
        RSAKeyPair keyPair = ARSAKeyPair();

        byte[] previousTransaction = {1, 2, 3};
        int indexOfUnspentOutputInPreviousTx = 4;
        UTXO utxo = new UTXO(previousTransaction, indexOfUnspentOutputInPreviousTx);
        UTXOPool utxoPool = new UTXOPool();
        utxoPool.addUTXO(utxo, ATransactionOutput(10, keyPair.getPublicKey()));
        MaxFeeTxHandler txHandler = new MaxFeeTxHandler(utxoPool);

        RSAKey receiverA = ARSAKey();
        double valueForA = 10;
        byte[] txHashPayingToA = {4, 5, 6};
        Transaction transactionWithLowFees = TransactionWithOneInputAndOneOutput(previousTransaction, indexOfUnspentOutputInPreviousTx, keyPair.getPrivateKey(), receiverA, valueForA, txHashPayingToA);

        RSAKey receiverB = ARSAKey();
        double valueForB = 5;
        byte[] txHashPayingToB = {7, 8, 9};
        Transaction transactionWithHighFees = TransactionWithOneInputAndOneOutput(previousTransaction, indexOfUnspentOutputInPreviousTx, keyPair.getPrivateKey(), receiverB, valueForB, txHashPayingToB);

        Transaction[] actualTransactions = txHandler.handleTxs(new Transaction[]{transactionWithLowFees, transactionWithHighFees});

        assertThat(actualTransactions.length, equalTo(1));
        assertThat(actualTransactions[0], equalTo(transactionWithHighFees));
    }
}