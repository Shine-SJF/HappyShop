package ci553.happyshop.atm;

// Bank class - implementation of a bank with a list of bank accounts and
// a current account that we are logged in to.

import java.util.ArrayList;


public class Bank{
    // Bank information
    private ArrayList<BankAccount> accounts; // List of bank accounts
    private BankAccount account = null; // Currently logged in account (null if no-one is logged in)

    public View view;

    // Constructor - provides example bank accounts
    public Bank()
    {
        Debug.trace("Bank::<constructor>");
        accounts = new ArrayList<>();
    }

    // Create a new BankAccount
    public BankAccount makeBankAccount(int accNumber, int accPasswd, int balance, String type)
    {
        return new BankAccount(accNumber, accPasswd, balance, type);
    }

    // Add a new bank account to the bank
    public boolean addBankAccount(BankAccount a)
    {
        accounts.add(a);
        Debug.trace("Bank::addBankAccount: added " + a.accNumber + " " + a.accPasswd + " Â£" + a.balance);
        return true;
    }

    // Add a new bank account with given details
    public boolean addBankAccount(int accNumber, int accPasswd, int balance, String type)
    {
        return addBankAccount(makeBankAccount(accNumber, accPasswd, balance, type));
    }

    // Log in to a bank account
    public boolean login(int newAccNumber, int newAccPasswd)
    {
        Debug.trace("Bank::login: accNumber = " + newAccNumber);
        logout(); // Log out of any previous account

        // Search for a matching account
        for (BankAccount acc : accounts) {
            if (acc.accNumber == newAccNumber && acc.accPasswd == newAccPasswd) {
                account = acc;
                Debug.trace("Bank::login: successful login to account " + newAccNumber);
                return true;
            }
        }

        // Not found
        Debug.trace("Bank::login: failed login attempt for account " + newAccNumber);
        account = null;
        return false;
    }

    // Check if an account exists with the given account number
    public boolean accountExists(int accNumber)
    {
        for (BankAccount acc : accounts) {
            if (acc.accNumber == accNumber) {
                return true;
            }
        }
        return false;
    }

    // Log out of the current account
    public void logout()
    {
        if (loggedIn())
        {
            Debug.trace("Bank::logout: logging out, accNumber = " + account.accNumber);
            account = null;
        }
    }

    // Check if logged in to an account
    public boolean loggedIn()
    {
        return account != null;
    }

    // Deposit money into the account
    public boolean deposit(int amount)
    {
        if (loggedIn()) {
            return account.deposit(amount);
        } else {
            return false;
        }
    }

    // Withdraw money from the account
    public boolean withdraw(int amount)
    {
        if (loggedIn()) {
            return account.withdraw(amount);
        } else {
            return false;
        }
    }

    // Get the account balance
    public int getBalance()
    {
        if (loggedIn()) {
            return account.getBalance();
        } else {
            return -1; // Use -1 as an indicator of an error
        }
    }

    // Change the password of the logged-in account
    public boolean changePassword(int oldPasswd, int newPasswd)
    {
        if (loggedIn()) {
            return account.changePassword(oldPasswd, newPasswd);
        } else {
            return false;
        }
    }

    // Validate the password of the logged-in account
    public boolean validatePassword(int passwd)
    {
        if (loggedIn()) {
            return account.accPasswd == passwd;
        } else {
            return false;
        }
    }
}