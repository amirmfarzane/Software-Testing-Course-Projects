package domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionEngineTest {

    private TransactionEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new TransactionEngine();
    }

    @Test
    public void testGetAverageTransactionAmountByAccount_EmptyHistory() {
        int avg = engine.getAverageTransactionAmountByAccount(1);
        assertEquals(0, avg);
    }

    @Test
    public void testGetAverageTransactionAmountByAccount_NoMatchingAccount() {
        Transaction txn1 = createTransaction(1, 2, 100, false);
        engine.transactionHistory.add(txn1);

        int avg = engine.getAverageTransactionAmountByAccount(1);
        assertEquals(0, avg);
    }

    @Test
    public void testGetAverageTransactionAmountByAccount_WithMatchingAccount() {
        Transaction txn1 = createTransaction(1, 1, 100, false);
        Transaction txn2 = createTransaction(2, 1, 200, false);
        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);

        int avg = engine.getAverageTransactionAmountByAccount(1);
        assertEquals(150, avg);
    }

    @Test
    public void testGetTransactionPatternAboveThreshold_EmptyHistory() {
        int pattern = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, pattern);
    }

    @Test
    public void testGetTransactionPatternAboveThreshold_SingleTransaction() {
        Transaction txn1 = createTransaction(1, 1, 2000, false);
        engine.transactionHistory.add(txn1);

        int pattern = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, pattern);
    }

    @Test
    public void testGetTransactionPatternAboveThreshold_PatternExists() {
        Transaction txn1 = createTransaction(1, 1, 1500, false);
        Transaction txn2 = createTransaction(2, 1, 2000, false);
        Transaction txn3 = createTransaction(3, 1, 2500, false);
        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);
        engine.transactionHistory.add(txn3);

        int pattern = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(500, pattern);
    }

    @Test
    public void testGetTransactionPatternAboveThreshold_PatternExistsWhileOneTransactionIsUnderThreshold() {
        Transaction txn1 = createTransaction(1, 1, 1200, false);
        Transaction txn2 = createTransaction(2, 1, 500, false);
        Transaction txn3 = createTransaction(3, 1, 1500, false);
        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);
        engine.transactionHistory.add(txn3);

        int pattern = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(300, pattern);
    }

    @Test
    public void testGetTransactionPatternAboveThreshold_NoPattern() {
        Transaction txn1 = createTransaction(1, 1, 1500, false);
        Transaction txn2 = createTransaction(2, 1, 2100, false);
        Transaction txn3 = createTransaction(3, 1, 2500, false);
        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);
        engine.transactionHistory.add(txn3);

        int pattern = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, pattern);
    }

    @Test
        public void testDetectFraudulentTransaction_NoTransactions() {
        Transaction txn = createTransaction(1, 1, 1000, true);
        int fraudScore = engine.detectFraudulentTransaction(txn);
        assertEquals(1000, fraudScore);
    }

    @Test
    public void testDetectFraudulentTransaction_NotDebit() {
        Transaction existingTxn = createTransaction(1, 1, 500, false);
        engine.transactionHistory.add(existingTxn);

        Transaction txn = createTransaction(2, 1, 2000, false);
        int fraudScore = engine.detectFraudulentTransaction(txn);
        assertEquals(0, fraudScore);
    }

    @Test
    public void testAddTransactionAndDetectFraud_TransactionExists() {
        Transaction txn = createTransaction(1, 1, 1000, true);
        engine.transactionHistory.add(txn);

        int fraudScore = engine.addTransactionAndDetectFraud(txn);
        assertEquals(0, fraudScore);
    }

    @Test
    public void testAddTransactionAndDetectFraud_NoFraud() {
        Transaction txn = createTransaction(2, 1, 500, true);
        int fraudScore = engine.addTransactionAndDetectFraud(txn);
        assertEquals(500, fraudScore);
    }

    @Test
    public void testAddTransactionAndDetectFraud_FraudulentTransaction() {
        Transaction existingTxn = createTransaction(1, 1, 500, false);
        engine.transactionHistory.add(existingTxn);

        Transaction txn = createTransaction(2, 1, 1100, true);
        int fraudScore = engine.addTransactionAndDetectFraud(txn);
        assertEquals(100, fraudScore);
    }

    @Test
    public void testAddTransactionAndDetectFraud_FraudulentPattern() {
        Transaction txn1 = createTransaction(1, 1, 1500, false);
        Transaction txn2 = createTransaction(2, 1, 2000, false);
        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);

        Transaction txn3 = createTransaction(3, 1, 2500, false);
        int fraudScore = engine.addTransactionAndDetectFraud(txn3);
        assertEquals(500, fraudScore);
    }


    @Test
    public void testDetectFraudulentTransaction_ExcessiveDebit() {
        Transaction txn1 = createTransaction(1, 1, 250, false);
        Transaction txn2 = createTransaction(2, 1, 50, false);
        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);

        Transaction txn3 = createTransaction(3, 1, 1500, true);

        engine.transactionHistory.add(txn3);
        int fraudScore = engine.detectFraudulentTransaction(txn3);
        assertEquals(300, fraudScore);
    }

    @Test
    public void testDetectFraudulentTransaction_ExcessiveDebitSkip() {
        Transaction txn1 = createTransaction(1, 1, 350, false);
        Transaction txn2 = createTransaction(2, 1, 550, false);
        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);

        Transaction txn3 = createTransaction(3, 1, 1500, true);

        engine.transactionHistory.add(txn3);
        int fraudScore = engine.detectFraudulentTransaction(txn3);
        assertEquals(0, fraudScore);
    }

    private Transaction createTransaction(int transactionId, int accountId, int amount, boolean isDebit) {
        Transaction txn = new Transaction();
        txn.setTransactionId(transactionId);
        txn.setAccountId(accountId);
        txn.setAmount(amount);
        txn.setIsDebit(isDebit);
        return txn;
    }

}
