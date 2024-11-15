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
        Transaction txn1 = new Transaction();
        txn1.setAccountId(2);
        txn1.setAmount(100);
        engine.transactionHistory.add(txn1);

        int avg = engine.getAverageTransactionAmountByAccount(1);
        assertEquals(0, avg);
    }

    @Test
    public void testGetAverageTransactionAmountByAccount_WithMatchingAccount() {
        Transaction txn1 = new Transaction();
        txn1.setAccountId(1);
        txn1.setAmount(100);
        Transaction txn2 = new Transaction();
        txn2.setAccountId(1);
        txn2.setAmount(200);
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
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAmount(2000);
        engine.transactionHistory.add(txn1);

        int pattern = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, pattern);
    }

    @Test
    public void testGetTransactionPatternAboveThreshold_PatternExists() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAmount(1500);
        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAmount(2000);
        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAmount(2500);

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);
        engine.transactionHistory.add(txn3);

        int pattern = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(500, pattern);
    }

    @Test
    public void testGetTransactionPatternAboveThreshold_NoPattern() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAmount(1500);
        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAmount(2100);
        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAmount(2500);

        engine.transactionHistory.add(txn1);
        engine.transactionHistory.add(txn2);
        engine.transactionHistory.add(txn3);

        int pattern = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, pattern);
    }

    @Test
    public void testDetectFraudulentTransaction_NoTransactions() {
        Transaction txn = new Transaction();
        txn.setAccountId(1);
        txn.setAmount(1000);
        txn.setIsDebit(true);

        int fraudScore = engine.detectFraudulentTransaction(txn);
        assertEquals(1000, fraudScore);
    }

    @Test
    public void testDetectFraudulentTransaction_NotDebit() {
        Transaction existingTxn = new Transaction();
        existingTxn.setAccountId(1);
        existingTxn.setAmount(500);
        engine.transactionHistory.add(existingTxn);

        Transaction txn = new Transaction();
        txn.setAccountId(1);
        txn.setAmount(2000);
        txn.setIsDebit(false);

        int fraudScore = engine.detectFraudulentTransaction(txn);
        assertEquals(0, fraudScore);
    }

    @Test
    public void testDetectFraudulentTransaction_DebitExcessive() {
        Transaction existingTxn = new Transaction();
        existingTxn.setAccountId(1);
        existingTxn.setAmount(500);
        engine.transactionHistory.add(existingTxn);

        Transaction txn = new Transaction();
        txn.setAccountId(1);
        txn.setAmount(1100);
        txn.setIsDebit(true);

        int fraudScore = engine.detectFraudulentTransaction(txn);
        assertEquals(100, fraudScore);
    }

}
