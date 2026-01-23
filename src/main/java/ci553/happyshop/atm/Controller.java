package ci553.happyshop.atm;

// The ATM controller processes button presses and calls methods in the model.

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.Stage;

    public class Controller
    {
        public Model model;
        public View view;

        public Controller()
        {
            Debug.trace("Controller::<constructor>");
        }

        // Process button presses
        public void process(String action) {
            Debug.trace("Controller::process: action = " + action);
            switch (action) {
                case "1":
                case "2": case "3": case "4": case "5":
                case "6": case "7": case "8": case "9": case "0":
                    model.processNumber(action);
                    break;
                case "CLR":
                    model.processClear();
                    break;
                case "Ent":
                    model.processEnter();
                    break;
                case "Wtd":
                    model.processWithdraw();
                    break;
                case "Dep":
                    model.processDeposit();
                    break;
                case "Bal":
                    model.processBalance();
                    break;
                case "Fin":
                    model.processFinish();
                    break;
                case "Acc":
                    if (model.state.equals(model.LOGGED_IN)) {
                        view.showPasswordChangeWindow();
                    } else {
                        view.showAccountCreationWindow();
                    }
                    break;
                case "MM":
                    if (model.state.equals(model.LOGGED_IN)) {
                        view.start((Stage) view.grid.getScene().getWindow());
                        model.display();
                    } else {
                        model.initialise("Welcome to the ATM");
                        view.start((Stage) view.grid.getScene().getWindow());
                        model.display();
                    }
                    break;
                default:
                    model.processUnknownKey(action);
                    break;
            }
        }

        // Handles the changing of passwords of existing accounts
        public void processPasswordChange(String action, TextField passwordMessage, TextArea passwordReply, Stage passwordStage) {
            Debug.trace("Controller::processPasswordChange: action = " + action);
            switch (action) {
                case "1":
                case "2": case "3": case "4": case "5":
                case "6": case "7": case "8": case "9": case "0":
                    model.processNumber(action);
                    passwordMessage.setText(String.valueOf(model.number));
                    break;
                case "CLR":
                    model.processClear();
                    passwordMessage.setText("");
                    break;
                case "Ent":
                    if (model.state.equals(model.VALIDATE_OLD_PASSWORD)) {
                        model.setOldPassword(model.number);
                        if (model.validateOldPassword()) {
                            model.setState(model.NEW_PASSWORD);
                            passwordReply.setText("Validated, please enter your new password (5 characters max).");
                            model.processClear();
                        } else {
                            passwordReply.setText("Incorrect old password. Try again.");
                            model.processClear();
                        }
                    } else if (model.state.equals(model.NEW_PASSWORD)) {
                        if (String.valueOf(model.number).length() == 5) {
                            model.changePassword(model.number);
                            passwordReply.setText("Password changed successfully. Close the window.");
                            model.processClear();
                        } else {
                            passwordReply.setText("Password must be 5 characters. Try again.");
                            model.processClear();
                        }
                    }
                    passwordMessage.setText("");
                    break;
                default:
                    model.processUnknownKey(action);
                    break;
            }
        }

        // Handle account creation process
        public void processAccountCreation(String action, TextField accountMessage, TextArea accountReply, Stage accountStage) {
            Debug.trace("Controller::processAccountCreation: action = " + action);
            switch (action) {
                case "B":
                case "P":
                    if (model.state.equals(model.SELECT_ACCOUNT_TYPE)) {
                        model.newAccountType = action.equals("B") ? "Basic" : "Premium";
                        model.setState(model.CREATE_ACCOUNT_NUMBER);
                        accountReply.setText("Now enter your desired account number with a max length of 5 characters.");
                        model.processClear();
                    }
                    break;
                case "1":
                case "2": case "3": case "4": case "5":
                case "6": case "7": case "8": case "9": case "0":
                    model.processNumber(action);
                    accountMessage.setText(String.valueOf(model.number));
                    break;
                case "CLR":
                    model.processClear();
                    accountMessage.setText("");
                    break;
                case "Ent":
                    if (model.state.equals(model.CREATE_ACCOUNT_NUMBER)) {
                        if (String.valueOf(model.number).length() == 5 && !model.bank.accountExists(model.number)) {
                            model.newAccountNumber = model.number;
                            model.setState(model.CREATE_ACCOUNT_PASSWORD);
                            accountReply.setText("Now please enter your desired account password with a character length of 5.");
                            model.processClear();
                        } else {
                            accountReply.setText("Invalid account number or account number already exists. Try again.");
                            model.processClear();
                        }
                    } else if (model.state.equals(model.CREATE_ACCOUNT_PASSWORD)) {
                        if (String.valueOf(model.number).length() == 5) {
                            model.newAccountPassword = model.number;
                            model.setState(model.CONFIRM_ACCOUNT_PASSWORD);
                            accountReply.setText("Please re-enter the same password.");
                            model.processClear();
                        } else {
                            accountReply.setText("Password must be 5 characters. Try again.");
                            model.processClear();
                        }
                    } else if (model.state.equals(model.CONFIRM_ACCOUNT_PASSWORD)) {
                        if (model.newAccountPassword == model.number) {
                            model.bank.addBankAccount(model.newAccountNumber, model.newAccountPassword, 0, model.newAccountType);
                            accountReply.setText("Account creation complete. Please close this window.");
                            model.processClear();
                            model.setState(model.ACCOUNT_NO);
                            accountStage.close();
                        } else {
                            accountReply.setText("This input does not match your previous input. Try again.");
                            model.processClear();
                        }
                    }
                    accountMessage.setText("");
                    break;
                default:
                    model.processUnknownKey(action);
                    break;
            }
        }
    }
