package domain;

//import org.junit.jupiter;

import domain.Transaction;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionTest {

    @Test
    public void testEqualsSameObject() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        assertTrue(txn.equals(txn));
    }

    @Test
    public void testEqualsSameTransactionId() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        Transaction txn2 = new Transaction();
        txn2.setTransactionId(1);
        assertTrue(txn1.equals(txn2));
    }

    @Test
    public void testEqualsDifferentTransactionId() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        assertFalse(txn1.equals(txn2));
    }

    @Test
    public void testEqualsNull() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        assertFalse(txn.equals(null));
    }

    @Test
    public void testEqualsDifferentObjectType() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        String str = "test";
        assertFalse(txn.equals(str));
    }
}
