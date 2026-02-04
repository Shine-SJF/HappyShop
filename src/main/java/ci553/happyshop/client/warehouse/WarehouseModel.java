package ci553.happyshop.client.warehouse;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.storageAccess.ImageFileManager;
import ci553.happyshop.utility.StorageLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * the bysiness logic layer for warehouse management in the MVC patter
 * this model handles alll product catalog oeprations, stock management, validation
 * and coordination betweeen the databse and teh warehosue view
 */
public class WarehouseModel {
    public WarehouseView view;
    public DatabaseRW databaseRW;

    private ArrayList<Product> productList = new ArrayList<>();
    private Product theSelectedPro;
    private String theNewProId;

    // Information used to update editProduct child in WarehouseView
    private String displayIdEdit = "";
    private String displayPriceEdit = "";
    private String displayStockEdit = "";
    private String displayDescriptionEdit = "";
    private String displayImageUrlEdit = "WarehouseImageHolder.jpg";

    public HistoryWindow historyWindow;
    public AlertSimulator alertSimulator;
    private String displayInputErrorMsg = "";
    private ArrayList<String> displayManageHistory = new ArrayList<>();

    private enum ManageProductType {
        EDITED,
        DELETED,
        NEW
    }

    private enum UpdateForAction {
        BTN_SEARCH,
        BTN_LOAD_ALL,
        BTN_EDIT,
        BTN_DELETE,
        BTN_CHANGE_STOCK_BY,
        BTN_SUBMIT_EDIT,
        BTN_CANCEL_EDIT,
        BTN_CANCEL_NEW,
        BTN_SUBMIT_NEW,
        SHOW_INPUT_ERROR_MSG
    }

    /**
     * NEW: Load all products (for initial display)
     */
    void doLoadAll() throws SQLException {
        System.out.println("Loading all products...");
        // Get all products by searching with wildcard or getting all IDs
        productList.clear();

        // Try to load products with IDs from 0001 to 0100
        for (int i = 1; i <= 100; i++) {
            String productId = String.format("%04d", i);
            try {
                Product product = databaseRW.searchByProductId(productId);
                if (product != null) {
                    productList.add(product);
                }
            } catch (SQLException e) {
                // Product doesn't exist, continue
            }
        }

        System.out.println("Loaded " + productList.size() + " products");
        updateView(UpdateForAction.BTN_LOAD_ALL);
    }

    /**
     * Searches products by keyword (ID or name)
     */
    void doSearch() throws SQLException {
        String keyword = view.tfSearchKeyword.getText().trim();
        System.out.println("Searching for: '" + keyword + "'");

        if (!keyword.isEmpty()) {
            productList = databaseRW.searchProduct(keyword);
            System.out.println("Found " + productList.size() + " products");
        } else {
            // If empty search, load all products
            doLoadAll();
            return;
        }
        updateView(UpdateForAction.BTN_SEARCH);
    }

    /**
     * Deletes the selected product
     */
    void doDelete() throws SQLException, IOException {
        Product pro = view.obrLvProducts.getSelectionModel().getSelectedItem();
        if (pro != null) {
            theSelectedPro = pro;
            productList.remove(theSelectedPro);

            // Delete from database
            databaseRW.deleteProduct(theSelectedPro.getProductId());

            // Delete image file
            String imageName = theSelectedPro.getProductImageName();
            if (imageName != null && !imageName.isEmpty()) {
                try {
                    ImageFileManager.deleteImageFile(StorageLocation.imageFolder, imageName);
                } catch (IOException e) {
                    System.err.println("Warning: Could not delete image file: " + e.getMessage());
                }
            }

            updateView(UpdateForAction.BTN_DELETE);
            theSelectedPro = null;
            showAlert("Product deleted successfully", "Success");
        } else {
            showAlert("No product selected for deletion", "Selection Error");
        }
    }

    /**
     * Prepares the edit form with selected product data
     */
    void doEdit() {
        Product pro = view.obrLvProducts.getSelectionModel().getSelectedItem();
        if (pro != null) {
            theSelectedPro = pro;
            displayIdEdit = theSelectedPro.getProductId();
            displayPriceEdit = String.format("%.2f", theSelectedPro.getUnitPrice());
            displayStockEdit = String.valueOf(theSelectedPro.getStockQuantity());
            displayDescriptionEdit = theSelectedPro.getProductDescription();

            // Build image URL
            String relativeImageUri = StorageLocation.imageFolder + theSelectedPro.getProductImageName();
            Path imageFullPath = Paths.get(relativeImageUri).toAbsolutePath();
            displayImageUrlEdit = imageFullPath.toUri().toString();

            updateView(UpdateForAction.BTN_EDIT);
        } else {
            showAlert("Please select a product to edit", "Selection Error");
        }
    }

    /**
     * Cancels current operation (edit or add new)
     */
    void doCancel() {
        if (view.theProFormMode.equals("EDIT")) {
            updateView(UpdateForAction.BTN_CANCEL_EDIT);
            theSelectedPro = null;
        } else if (view.theProFormMode.equals("NEW")) {
            updateView(UpdateForAction.BTN_CANCEL_NEW);
            theNewProId = null;
        }
    }

    /**
     * Submits form based on current mode (edit or add new)
     */
    void doSummit() throws SQLException, IOException {
        System.out.println("Submit called, mode: " + view.theProFormMode);
        if (view.theProFormMode.equals("EDIT")) {
            doSubmitEdit();
        } else if (view.theProFormMode.equals("NEW")) {
            doSubmitNew();
        }
    }

    /**
     * Submits edit form
     */
    private void doSubmitEdit() throws IOException, SQLException {
        if (theSelectedPro != null) {
            String id = theSelectedPro.getProductId();
            String currentImageName = theSelectedPro.getProductImageName();

            String textPrice = view.tfPriceEdit.getText().trim();
            String textStock = view.tfStockEdit.getText().trim();
            String description = view.taDescriptionEdit.getText().trim();

            System.out.println("Submitting edit for product: " + id);
            System.out.println("Price: " + textPrice + ", Stock: " + textStock);

            // Handle image changes
            String newImageName = currentImageName;
            if (view.isUserSelectedImageEdit) {
                // Delete old image
                try {
                    ImageFileManager.deleteImageFile(StorageLocation.imageFolder, currentImageName);
                } catch (IOException e) {
                    System.err.println("Warning: Could not delete old image: " + e.getMessage());
                }

                // Copy new image
                newImageName = ImageFileManager.copyFileToDestination(
                        view.userSelectedImageUriEdit,
                        StorageLocation.imageFolder,
                        id
                );
            }

            // Validate input
            if (!validateInputEditChild(textPrice, textStock, description)) {
                updateView(UpdateForAction.SHOW_INPUT_ERROR_MSG);
                return;
            }

            // Parse validated input
            double price = Double.parseDouble(textPrice);
            int stock = Integer.parseInt(textStock);

            // Update database
            databaseRW.updateProduct(id, description, price, newImageName, stock);

            // Update view and show success
            updateView(UpdateForAction.BTN_SUBMIT_EDIT);
            theSelectedPro = null;
            showAlert("Product updated successfully", "Success");

            // Reload all products to show updated data
            doLoadAll();
        } else {
            showAlert("No product selected for editing", "Selection Error");
        }
    }

    /**
     * Changes stock by specified amount (add or subtract)
     */
    void doChangeStockBy(String addOrSub) throws SQLException {
        try {
            int oldStock = Integer.parseInt(view.tfStockEdit.getText().trim());
            String textChangeBy = view.tfChangeByEdit.getText().trim();

            if (!textChangeBy.isEmpty()) {
                if (!validateInputChangeStockBy(textChangeBy)) {
                    updateView(UpdateForAction.SHOW_INPUT_ERROR_MSG);
                    return;
                }

                int changeBy = Integer.parseInt(textChangeBy);
                int newStock = oldStock;

                switch (addOrSub.toLowerCase()) {
                    case "add":
                        newStock = oldStock + changeBy;
                        break;
                    case "sub":
                        newStock = oldStock - changeBy;
                        if (newStock < 0) {
                            showAlert("Stock cannot be negative", "Validation Error");
                            return;
                        }
                        break;
                }

                displayStockEdit = String.valueOf(newStock);
                updateView(UpdateForAction.BTN_CHANGE_STOCK_BY);
                view.tfChangeByEdit.clear();
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid stock value", "Validation Error");
        }
    }

    /**
     * Validates stock change input
     */
    private boolean validateInputChangeStockBy(String txChangeBy) {
        StringBuilder errorMessage = new StringBuilder();

        try {
            int changeBy = Integer.parseInt(txChangeBy);
            if (changeBy <= 0) {
                errorMessage.append("• Change amount must be a positive integer.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("• Invalid number format for stock change.\n");
        }

        if (errorMessage.length() > 0) {
            displayInputErrorMsg = errorMessage.toString();
            return false;
        }
        return true;
    }

    /**
     * Submits new product form
     */
    private void doSubmitNew() throws SQLException, IOException {
        // Get input values
        theNewProId = view.tfIdNewPro.getText().trim();
        String textPrice = view.tfPriceNewPro.getText().trim();
        String textStock = view.tfStockNewPro.getText().trim();
        String description = view.taDescriptionNewPro.getText().trim();
        String imagePath = view.imageUriNewPro;

        System.out.println("Submitting new product:");
        System.out.println("ID: " + theNewProId);
        System.out.println("Price: " + textPrice);
        System.out.println("Stock: " + textStock);
        System.out.println("Description: " + description);
        System.out.println("Image: " + imagePath);

        // Validate input
        if (!validateInputNewProChild(theNewProId, textPrice, textStock, description, imagePath)) {
            updateView(UpdateForAction.SHOW_INPUT_ERROR_MSG);
            return;
        }

        // Copy image to destination
        String imageNameWithExtension = ImageFileManager.copyFileToDestination(
                imagePath,
                StorageLocation.imageFolder,
                theNewProId
        );

        // Parse validated values
        double price = Double.parseDouble(textPrice);
        int stock = Integer.parseInt(textStock);

        // Insert into database
        databaseRW.insertNewProduct(theNewProId, description, price, imageNameWithExtension, stock);

        // Update view and show success
        updateView(UpdateForAction.BTN_SUBMIT_NEW);
        theNewProId = null;
        showAlert("New product added successfully", "Success");

        // Reload all products to show new product
        doLoadAll();
    }

    /**
     * Validates edit form input
     */
    private boolean validateInputEditChild(String txPrice, String txStock, String description) {
        StringBuilder errorMessage = new StringBuilder();

        // Validate price
        try {
            double price = Double.parseDouble(txPrice);
            if (price <= 0) {
                errorMessage.append("• Price must be a positive number.\n");
            }
            if (!txPrice.matches("^[0-9]+(\\.[0-9]{0,2})?$")) {
                errorMessage.append("• Price can have at most two decimal places.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("• Invalid price format.\n");
        }

        // Validate stock
        try {
            int stock = Integer.parseInt(txStock);
            if (stock < 0) {
                errorMessage.append("• Stock quantity cannot be negative.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("• Invalid stock quantity format.\n");
        }

        // Validate description
        if (description.isEmpty()) {
            errorMessage.append("• Product description cannot be empty.\n");
        }

        // Check for unapplied stock changes
        if (!view.tfChangeByEdit.getText().trim().isEmpty()) {
            errorMessage.append("• Please apply or clear stock changes before submitting.\n");
        }

        if (errorMessage.length() > 0) {
            displayInputErrorMsg = errorMessage.toString();
            return false;
        }
        return true;
    }

    /**
     * Validates new product form input
     */
    private boolean validateInputNewProChild(String id, String txPrice, String txStock,
                                             String description, String imageUri) throws SQLException {
        StringBuilder errorMessage = new StringBuilder();

        // Validate ID
        if (id == null || !id.matches("\\d{4}")) {
            errorMessage.append("• Product ID must be exactly 4 digits.\n");
        }

        // Check ID availability
        if (id != null && !databaseRW.isProIdAvailable(id)) {
            errorMessage.append("• Product ID " + id + " is already in use.\n");
        }

        // Validate price
        try {
            double price = Double.parseDouble(txPrice);
            if (price <= 0) {
                errorMessage.append("• Price must be a positive number.\n");
            }
            if (!txPrice.matches("^[0-9]+(\\.[0-9]{0,2})?$")) {
                errorMessage.append("• Price can have at most two decimal places.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("• Invalid price format.\n");
        }

        // Validate stock
        try {
            int stock = Integer.parseInt(txStock);
            if (stock < 0) {
                errorMessage.append("• Stock quantity cannot be negative.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("• Invalid stock quantity format.\n");
        }

        // Validate description
        if (description.isEmpty()) {
            errorMessage.append("• Product description cannot be empty.\n");
        }

        // Validate image
        if (imageUri == null || imageUri.isEmpty()) {
            errorMessage.append("• Please select an image for the product.\n");
        }

        if (errorMessage.length() > 0) {
            displayInputErrorMsg = errorMessage.toString();
            return false;
        }
        return true;
    }

    /**
     * Updates the view based on the action
     */
    private void updateView(UpdateForAction updateFor) {
        switch (updateFor) {
            case BTN_LOAD_ALL:
            case BTN_SEARCH:
                view.updateObservableProductList(productList);
                break;
            case BTN_EDIT:
                view.updateEditProductChild(
                        displayIdEdit,
                        displayPriceEdit,
                        displayStockEdit,
                        displayDescriptionEdit,
                        displayImageUrlEdit
                );
                break;
            case BTN_DELETE:
                view.updateObservableProductList(productList);
                showManageStockHistory(ManageProductType.DELETED);
                view.resetEditChild();
                closeAlertWindow();
                break;
            case BTN_CHANGE_STOCK_BY:
                view.updateBtnAddSub(displayStockEdit);
                closeAlertWindow();
                break;
            case BTN_CANCEL_EDIT:
                view.resetEditChild();
                closeAlertWindow();
                break;
            case BTN_SUBMIT_EDIT:
                showManageStockHistory(ManageProductType.EDITED);
                view.resetEditChild();
                closeAlertWindow();
                break;
            case BTN_CANCEL_NEW:
                view.resetNewProChild();
                closeAlertWindow();
                break;
            case BTN_SUBMIT_NEW:
                showManageStockHistory(ManageProductType.NEW);
                view.resetNewProChild();
                closeAlertWindow();
                break;
            case SHOW_INPUT_ERROR_MSG:
                if (alertSimulator != null) {
                    alertSimulator.showErrorMsg(displayInputErrorMsg);
                }
                break;
        }
    }

    /**
     * Adds entry to manage history
     */
    private void showManageStockHistory(ManageProductType type) {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String record = "";

        switch (type) {
            case EDITED:
                if (theSelectedPro != null) {
                    record = String.format("✓ Edited product %s - %s (%s)",
                            theSelectedPro.getProductId(),
                            theSelectedPro.getProductDescription(),
                            dateTime);
                }
                break;
            case DELETED:
                if (theSelectedPro != null) {
                    record = String.format("✗ Deleted product %s - %s (%s)",
                            theSelectedPro.getProductId(),
                            theSelectedPro.getProductDescription(),
                            dateTime);
                }
                break;
            case NEW:
                if (theNewProId != null) {
                    record = String.format("➕ Added new product %s (%s)",
                            theNewProId,
                            dateTime);
                }
                break;
        }

        if (!record.isEmpty()) {
            displayManageHistory.add(record);
            if (historyWindow != null) {
                historyWindow.showManageHistory(displayManageHistory);
            }
        }
    }

    /**
     * Shows an alert message
     */
    private void showAlert(String message, String title) {
        if (alertSimulator != null) {
            alertSimulator.showInfoMsg(message, title);
        } else {
            System.out.println(title + ": " + message);
        }
    }

    /**
     * Closes alert window if open
     */
    private void closeAlertWindow() {
        if (alertSimulator != null) {
            alertSimulator.closeAlertSimulatorWindow();
        }
    }
}