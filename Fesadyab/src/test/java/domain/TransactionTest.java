package domain;

//import org.junit.jupiter;

import domain.Transaction;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionTest {

    @Test
    public void testEqualsSameObject() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        assertTrue(txn.equals(txn));
    }

}
