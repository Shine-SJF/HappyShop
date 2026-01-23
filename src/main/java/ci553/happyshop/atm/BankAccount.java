package ci553.happyshop.atm;

public class BankAccount {// BankAccount class
// This class has instance variables for the account number, password, balance, and methods
// to withdraw, deposit, check balance, etc.

        // Declare account details variables
        public int accNumber = 0;
        public int accPasswd = 0;
        public int balance = 0;
        public String accountType = "Basic"; // Default account type

        // Withdrawal limits and overdrafts
        private static final int BASIC_WITHDRAWAL_LIMIT = 500;
        private static final int BASIC_OVERDRAFT_LIMIT = 100;
        private static final int PREMIUM_WITHDRAWAL_LIMIT = 1500;
        private static final int PREMIUM_OVERDRAFT_LIMIT = 500;

        // Template for creating and managing a bank account
        public BankAccount(int n, int p, int b, String type)
        {
            accNumber = n;
            accPasswd = p;
            balance = b;
            accountType = type;
        }

        // Withdraw money from the account. Return true if successful, or
        // false if the amount is negative, exceeds balance, or exceeds withdrawal limit
        public boolean withdraw(int amount)
        {
            int withdrawalLimit = accountType.equals("Premium") ? PREMIUM_WITHDRAWAL_LIMIT : BASIC_WITHDRAWAL_LIMIT;
            int overdraftLimit = accountType.equals("Premium") ? PREMIUM_OVERDRAFT_LIMIT : BASIC_OVERDRAFT_LIMIT;

            if (amount > 0 && amount <= withdrawalLimit && (balance + overdraftLimit) >= amount) {
                balance -= amount;
                return true;
            } else {
                return false;
            }
        }

        // Deposit money into the account. Return true if successful,
        // or false if the amount is negative
        public boolean deposit(int amount)
        {
            if (amount > 0) {
                balance += amount;
                return true;
            } else {
                return false;
            }
        }

        // Return the current balance in the account
        public int getBalance()
        {
            return balance;
        }

        // Change the password of the account. Return true if successful,
        // or false if the old password is incorrect
        public boolean changePassword(int oldPasswd, int newPasswd)
        {
            if (accPasswd == oldPasswd) {
                accPasswd = newPasswd;
                return true;
            } else {
                return false;
            }
        }
    }

