package ci553.happyshop.atm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ATM BankAccount.
 * These give us confidence that payments based on withdraw/deposit behave correctly.
 */
public class BankAccountTest {

    @Test
    void withdrawReducesBalanceWhenSufficientFunds() {
        BankAccount acc = new BankAccount(1234, 1111, 500, "current");

        boolean ok = acc.withdraw(100);

        assertTrue(ok);
        assertEquals(400, acc.getBalance());
    }

    @Test
    void withdrawFailsWhenInsufficientFunds() {
        BankAccount acc = new BankAccount(1234, 1111, 200, "current");

        boolean ok = acc.withdraw(500);

        assertFalse(ok);
        assertEquals(200, acc.getBalance(), "Balance should not change if withdraw fails");
    }

    @Test
    void depositIncreasesBalance() {
        BankAccount acc = new BankAccount(1234, 1111, 50, "current");

        boolean ok = acc.deposit(25);

        assertTrue(ok);
        assertEquals(75, acc.getBalance());
    }
}
