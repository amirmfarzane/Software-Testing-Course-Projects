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

}
