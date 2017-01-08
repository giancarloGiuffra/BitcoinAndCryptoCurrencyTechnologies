import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TxHandlerTest {

    @Test
    public void ShouldInitializeTxHandlerWithUTXOPool() throws Exception {
        UTXO utxo = new UTXO(new byte[]{1, 2, 3}, 4);
        UTXOPool utxoPool = new UTXOPool();
        utxoPool.addUTXO(utxo, ATransactionOutput(23.5, ARSAKey()));
        TxHandler txHandler = new TxHandler(utxoPool);

        UTXOPool utxoPoolFromTxHandler = txHandler.getUTXOPool();
        assertThat(utxoPoolFromTxHandler.getAllUTXO().size(), equalTo(1));
        assertThat(utxoPoolFromTxHandler.getAllUTXO().get(0), equalTo(utxo));
    }

    @Test
    public void TransactionIsValidIfAllOutputsInUTXOPool() throws Exception {
        TxHandler txHandler = new TxHandler(new UTXOPool());

        Transaction transaction = new Transaction();
        transaction.addInput(new byte[]{1, 2, 3}, 4);

        assertThat(txHandler.isValidTx(transaction), equalTo(false));
    }

    @Test
    public void TransactionValidIfInputSignaturesValidForTheirRespectiveOutputPublicKey() throws Exception {
        RSAKeyPair keyPair = ARSAKeyPair();

        byte[] previousTransaction = {1, 2, 3};
        int indexOfUnspentOutputInPreviousTx = 4;
        UTXO utxo = new UTXO(previousTransaction, indexOfUnspentOutputInPreviousTx);
        UTXOPool utxoPool = new UTXOPool();
        utxoPool.addUTXO(utxo, ATransactionOutput(23.5, keyPair.getPublicKey()));
        TxHandler txHandler = new TxHandler(utxoPool);

        Transaction transaction = new Transaction();
        transaction.addInput(previousTransaction, indexOfUnspentOutputInPreviousTx);
        transaction.addSignature(keyPair.getPrivateKey().sign(transaction.getRawDataToSign(0)), 0);

        assertThat(txHandler.isValidTx(transaction), equalTo(true));
    }

    @Test
    public void TransactionShouldNotClaimSameUTXOMoreThanOnce() throws Exception {
        RSAKeyPair keyPair = ARSAKeyPair();

        byte[] previousTransaction = {1, 2, 3};
        int indexOfUnspentOutputInPreviousTx = 4;
        UTXO utxo = new UTXO(previousTransaction, indexOfUnspentOutputInPreviousTx);
        UTXOPool utxoPool = new UTXOPool();
        utxoPool.addUTXO(utxo, ATransactionOutput(23.5, keyPair.getPublicKey()));
        TxHandler txHandler = new TxHandler(utxoPool);

        Transaction transaction = new Transaction();
        transaction.addInput(previousTransaction, indexOfUnspentOutputInPreviousTx);
        transaction.addInput(previousTransaction, indexOfUnspentOutputInPreviousTx);
        transaction.addSignature(keyPair.getPrivateKey().sign(transaction.getRawDataToSign(0)), 0);
        transaction.addSignature(keyPair.getPrivateKey().sign(transaction.getRawDataToSign(1)), 1);

        assertThat(txHandler.isValidTx(transaction), equalTo(false));
    }

    @Test
    public void TransactionOutputsAreAllNonNegative() throws Exception {
        RSAKeyPair keyPair = ARSAKeyPair();

        byte[] previousTransaction = {1, 2, 3};
        int indexOfUnspentOutputInPreviousTx = 4;
        UTXO utxo = new UTXO(previousTransaction, indexOfUnspentOutputInPreviousTx);
        UTXOPool utxoPool = new UTXOPool();
        utxoPool.addUTXO(utxo, ATransactionOutput(23.5, keyPair.getPublicKey()));
        TxHandler txHandler = new TxHandler(utxoPool);

        Transaction transaction = new Transaction();
        transaction.addInput(previousTransaction, indexOfUnspentOutputInPreviousTx);
        transaction.addOutput(-10, ARSAKey());
        transaction.addSignature(keyPair.getPrivateKey().sign(transaction.getRawDataToSign(0)), 0);

        assertThat(txHandler.isValidTx(transaction), equalTo(false));
    }

    @Test
    public void TransactionClaimedOutputsAreAtLeastEqualToNewOutputs() throws Exception {
        RSAKeyPair keyPair = ARSAKeyPair();

        byte[] previousTransaction = {1, 2, 3};
        int indexOfUnspentOutputInPreviousTx = 4;
        TxHandler txHandler = TxHandlerWithOneUTXOInPool(previousTransaction, indexOfUnspentOutputInPreviousTx, keyPair, 10);

        Transaction transaction = new Transaction();
        transaction.addInput(previousTransaction, indexOfUnspentOutputInPreviousTx);
        transaction.addOutput(20, ARSAKey());
        transaction.addSignature(keyPair.getPrivateKey().sign(transaction.getRawDataToSign(0)), 0);

        assertThat(txHandler.isValidTx(transaction), equalTo(false));
    }

    @Test
    public void ShoulReturnEmptySetOfTransactionsAndUTXOPoolUnmodifiedIfNoTransactions() throws Exception {
        RSAKeyPair keyPair = ARSAKeyPair();

        byte[] previousTransaction = {1, 2, 3};
        int indexOfUnspentOutputInPreviousTx = 4;
        UTXO utxo = new UTXO(previousTransaction, indexOfUnspentOutputInPreviousTx);
        UTXOPool utxoPool = new UTXOPool();
        utxoPool.addUTXO(utxo, ATransactionOutput(10, keyPair.getPublicKey()));
        TxHandler txHandler = new TxHandler(utxoPool);

        assertThat(txHandler.handleTxs(new Transaction[]{}).length, equalTo(0));
        assertThat(txHandler.getUTXOPool().getAllUTXO(), equalTo(utxoPool.getAllUTXO()));
    }

    @Test
    public void ShouldProcessSingleTransaction() throws Exception {
        RSAKeyPair keyPair = ARSAKeyPair();

        byte[] previousTransaction = {1, 2, 3};
        int indexOfUnspentOutputInPreviousTx = 4;
        TxHandler txHandler = TxHandlerWithOneUTXOInPool(previousTransaction, indexOfUnspentOutputInPreviousTx, keyPair, 10);

        RSAKey receiver = ARSAKey();
        double value = 5;
        byte[] txHash = {4, 5, 6};
        Transaction transaction = TransactionWithOneInputAndOneOutput(previousTransaction, indexOfUnspentOutputInPreviousTx, keyPair.getPrivateKey(), receiver, value, txHash);

        Transaction[] actualTransactions = txHandler.handleTxs(new Transaction[]{transaction});
        UTXOPool newUTXOPool = txHandler.getUTXOPool();

        assertThat(actualTransactions.length, equalTo(1));
        assertThat(actualTransactions[0], equalTo(transaction));
        assertThat(newUTXOPool.getAllUTXO().size(), equalTo(1));
        assertThat(newUTXOPool.getAllUTXO().get(0), equalTo(new UTXO(transaction.getHash(), 0)));
        assertThat(newUTXOPool.getTxOutput(newUTXOPool.getAllUTXO().get(0)).value, equalTo(value));
        assertThat(newUTXOPool.getTxOutput(newUTXOPool.getAllUTXO().get(0)).address, equalTo(receiver));
    }

    @Test
    public void ShouldNotAllowDoubleSpendingTransaction() throws Exception {
        RSAKeyPair keyPair = ARSAKeyPair();

        byte[] previousTransaction = {1, 2, 3};
        int indexOfUnspentOutputInPreviousTx = 4;
        TxHandler txHandler = TxHandlerWithOneUTXOInPool(previousTransaction, indexOfUnspentOutputInPreviousTx, keyPair, 10);

        RSAKey receiverA = ARSAKey();
        double valueForA = 5;
        byte[] txHashPayingToA = {4, 5, 6};
        Transaction transactionPayingToA = TransactionWithOneInputAndOneOutput(previousTransaction, indexOfUnspentOutputInPreviousTx, keyPair.getPrivateKey(), receiverA, valueForA, txHashPayingToA);

        RSAKey receiverB = ARSAKey();
        double valueForB = 5;
        byte[] txHashPayingToB = {7, 8, 9};
        Transaction transactionPayingToB = TransactionWithOneInputAndOneOutput(previousTransaction, indexOfUnspentOutputInPreviousTx, keyPair.getPrivateKey(), receiverB, valueForB, txHashPayingToB);

        Transaction[] actualTransactions = txHandler.handleTxs(new Transaction[]{transactionPayingToA, transactionPayingToB});
        UTXOPool newUTXOPool = txHandler.getUTXOPool();

        assertThat(actualTransactions.length, equalTo(1));
        assertThat(actualTransactions[0], equalTo(transactionPayingToA));
        assertThat(newUTXOPool.getAllUTXO().size(), equalTo(1));
        assertThat(newUTXOPool.getAllUTXO().get(0), equalTo(new UTXO(transactionPayingToA.getHash(), 0)));
        assertThat(newUTXOPool.getTxOutput(newUTXOPool.getAllUTXO().get(0)).value, equalTo(valueForA));
        assertThat(newUTXOPool.getTxOutput(newUTXOPool.getAllUTXO().get(0)).address, equalTo(receiverA));
    }

    @Test
    public void ShouldIncludeTransactionsThatWereDiscardedFirstButThatThenBecameValid() throws Exception {
        RSAKeyPair keyPairOfA = ARSAKeyPair();
        RSAKeyPair keyPairOfB = ARSAKeyPair();
        RSAKeyPair keyPairOfC = ARSAKeyPair();

        byte[] previousTransaction = {1, 2, 3};
        int indexOfUnspentOutputInPreviousTx = 4;
        TxHandler txHandler = TxHandlerWithOneUTXOInPool(previousTransaction, indexOfUnspentOutputInPreviousTx, keyPairOfA, 10);

        double valueAPaysB = 10;
        byte[] txHashAPaysB = {4, 5, 6};
        Transaction transactionAPaysB = TransactionWithOneInputAndOneOutput(previousTransaction, indexOfUnspentOutputInPreviousTx, keyPairOfA.getPrivateKey(), keyPairOfB.getPublicKey(), valueAPaysB, txHashAPaysB);

        double valueBPaysC = 10;
        byte[] txHashBPaysC = {7, 8, 9};
        Transaction transactionBPaysC = TransactionWithOneInputAndOneOutput(txHashAPaysB, 0, keyPairOfB.getPrivateKey(), keyPairOfC.getPublicKey(), valueBPaysC, txHashBPaysC);

        double valueCPaysA = 5;
        byte[] txHashCPaysA = {10, 11, 12};
        Transaction transactionCPaysA = TransactionWithOneInputAndOneOutput(txHashBPaysC, 0, keyPairOfC.getPrivateKey(), keyPairOfA.getPublicKey(), valueCPaysA, txHashCPaysA);

        Transaction[] actualTransactions = txHandler.handleTxs(new Transaction[]{transactionAPaysB, transactionCPaysA, transactionBPaysC});

        assertThat(actualTransactions.length, equalTo(3));
        assertThat(actualTransactions[0], equalTo(transactionAPaysB));
        assertThat(actualTransactions[1], equalTo(transactionBPaysC));
        assertThat(actualTransactions[2], equalTo(transactionCPaysA));
    }

    protected RSAKeyPair ARSAKeyPair() {
        byte[] key = new byte[32];
        new Random().nextBytes(key);
        return new RSAKeyPair(new PRGen(key), 265);
    }

    protected TxHandler TxHandlerWithOneUTXOInPool(byte[] previousTransaction, int indexOfUnspentOutputInPreviousTx, RSAKeyPair keyPair, int value) {
        UTXO utxo = new UTXO(previousTransaction, indexOfUnspentOutputInPreviousTx);
        UTXOPool utxoPool = new UTXOPool();
        utxoPool.addUTXO(utxo, ATransactionOutput(value, keyPair.getPublicKey()));
        return new TxHandler(utxoPool);
    }

    protected Transaction TransactionWithOneInputAndOneOutput(byte[] previousTransaction, int indexOfUnspentOutputInPreviousTx, RSAKey payer, RSAKey receiver, double value, byte[] txHash) {
        Transaction transaction = new Transaction();
        transaction.addInput(previousTransaction, indexOfUnspentOutputInPreviousTx);
        transaction.addOutput(value, receiver);
        transaction.addSignature(payer.sign(transaction.getRawDataToSign(0)), 0);
        transaction.setHash(txHash);
        return transaction;
    }

    protected Transaction.Output ATransactionOutput(double value, RSAKey address) {
        return new Transaction().new Output(value, address);
    }

    protected RSAKey ARSAKey() {
        return new RSAKey(ABigInteger(), ABigInteger());
    }

    private BigInteger ABigInteger() {
        return new BigInteger(10, 5, new Random());
    }
}