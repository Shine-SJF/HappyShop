package ci553.happyshop.atm;

import javafx.stage.Stage;

    // The model represents the content and functionality of the app.
    public class Model
    {
        // ATM states
        final String ACCOUNT_NO = "account_no";
        final String PASSWORD = "password";
        final String LOGGED_IN = "logged_in";
        final String VALIDATE_OLD_PASSWORD = "validate_old_password";
        final String NEW_PASSWORD = "new_password";
        final String CREATE_ACCOUNT_NUMBER = "create_account_number";
        final String CREATE_ACCOUNT_PASSWORD = "create_account_password";
        final String CONFIRM_ACCOUNT_PASSWORD = "confirm_account_password";
        final String SELECT_ACCOUNT_TYPE = "select_account_type";
        final String WELCOME_PAGE = "welcome_page";
        final String SESSION_TERMINATED = "session_terminated";

        // ATM model variables
        String state = WELCOME_PAGE;
        int number = 0;
        Bank bank = null;
        int accNumber = -1;
        int accPasswd = -1;
        int oldPassword = -1;
        int newAccountNumber = -1;
        int newAccountPassword = -1;
        String newAccountType = "";
        String title = "Level Bank";
        String display1 = null;
        String display2 = null;

        public View view;
        public Controller controller;

        // Reference the bank object and class so Model is aware of its existence
        public Model(Bank b)
        {
            Debug.trace("Model::<constructor>");
            bank = b;
        }

        // Initialise the ATM
        public void initialise(String message) {
            setState(ACCOUNT_NO);
            number = 0;
            display1 = message;
            display2 = "Enter your account number\nFollowed by \"Ent\"";
        }

        // Change state
        public void setState(String newState)
        {
            if (!state.equals(newState))
            {
                String oldState = state;
                state = newState;
                Debug.trace("Model::setState: changed state from " + oldState + " to " + newState);
            }
        }

        // Process a number key
        public void processNumber(String label)
        {
            char c = label.charAt(0);
            number = number * 10 + c - '0';
            display1 = "" + number;
            display();
        }

        // Process the Clear button
        public void processClear()
        {
            number = 0;
            display1 = "";
            display();
        }

        // Process the Enter button
        public void processEnter()
        {
            switch (state)
            {
                case ACCOUNT_NO:
                    accNumber = number;
                    number = 0;
                    setState(PASSWORD);
                    display1 = "";
                    display2 = "Now enter your password\nFollowed by \"Ent\"";
                    break;
                case PASSWORD:
                    accPasswd = number;
                    number = 0;
                    display1 = "";
                    if (bank.login(accNumber, accPasswd))
                    {
                        setState(LOGGED_IN);
                        display2 = "Accepted\nNow enter the transaction you require\nRemember to check your balance before withdrawing.";
                    } else {
                        initialise("Unknown account/password");
                    }
                    break;
                case LOGGED_IN:
                default:
            }
            display();
        }

        // Process the Withdraw button
        public void processWithdraw()
        {
            if (state.equals(LOGGED_IN)) {
                if (bank.withdraw(number))
                {
                    display2 = "Withdrawn: " + number + "\nBalance: " + bank.getBalance();
                } else {
                    display2 = "You do not have sufficient funds";
                }
                number = 0;
                display1 = "";
            } else {
                initialise("You are not logged in");
            }
            display();
        }

        // Process the Deposit button
        public void processDeposit()
        {
            if (state.equals(LOGGED_IN)) {
                bank.deposit(number);
                display1 = "";
                display2 = "Deposited: " + number + "\nBalance: " + bank.getBalance();
                number = 0;
            } else {
                initialise("You are not logged in");
            }
            display();
        }

        // Process the Balance button
        public void processBalance()
        {
            if (state.equals(LOGGED_IN)) {
                number = 0;
                display2 = "Your balance is: " + bank.getBalance();
            } else {
                initialise("You are not logged in");
            }
            display();
        }

        // Process the Finish button
        public void processFinish()
        {
            if (state.equals(LOGGED_IN)) {
                Debug.trace("Model::processFinish");
                bank.logout();
                setState(SESSION_TERMINATED);
                Stage endedSession = (Stage) view.grid.getScene().getWindow();
                view.endSession(endedSession);
                number = 0;
                display2 = "Welcome: Enter your account number";
            } else {
                initialise("You are not logged in");
            }
            display();
        }

        // Process the Change Password button
        public void processChangePassword(int oldPasswd, int newPasswd)
        {
            setOldPassword(oldPasswd);
            changePassword(newPasswd);
        }

        // Set the old password
        public void setOldPassword(int oldPasswd)
        {
            oldPassword = oldPasswd;
        }

        // Change the password
        public void changePassword(int newPasswd)
        {
            if (bank.changePassword(oldPassword, newPasswd)) {
                display2 = "Password changed successfully";
            } else {
                display2 = "Failed to change password";
            }
            display();
        }

        // Validate the old password
        public boolean validateOldPassword()
        {
            return bank.validatePassword(oldPassword);
        }

        // Check if an account exists with the given account number
        public boolean accountExists(int accNumber)
        {
            return bank.accountExists(accNumber);
        }

        // Process an unknown key
        public void processUnknownKey(String action)
        {
            Debug.trace("Model::processUnknownKey: unknown button \"" + action + "\", re-initialising");
            initialise("Invalid command");
            display();
        }

        // Calls the update function in the View class
        public void display()
        {
            Debug.trace("Model::display");
            view.update();
        }
    }

