package pti.datenbank.autowerk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.Customer;
import pti.datenbank.autowerk.models.Role;
import pti.datenbank.autowerk.models.User;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.RoleService;
import pti.datenbank.autowerk.services.facade.UserFacade;

import java.sql.SQLException;

public class CustomerUserDialogController {
    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private TextField tfEmail;
    @FXML private TextField tfFullName;
    @FXML private TextField tfPhone;
    @FXML private TextField tfAddress;

    private Stage dialogStage;
    private UserFacade userFacade;
    private RoleService roleService;
    private AuthService authService;
    private boolean okClicked = false;
    private User editingUser;
    private Customer editingCustomer;

    public void setServices(AuthService authService, UserFacade userFacade) {
        this.authService = authService;
        this.userFacade  = userFacade;
        this.roleService = new RoleService(authService);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEditingData(User user, Customer customer) {
        this.editingUser     = user;
        this.editingCustomer = customer;

        if (user != null) {
            tfUsername.setText(user.getUsername());
            tfEmail.setText(user.getEmail());
        }
        if (customer != null) {
            tfFullName.setText(customer.getFullName());
            tfPhone.setText(customer.getPhone());
            tfAddress.setText(customer.getAddress());
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        StringBuilder err = new StringBuilder();
        if (tfUsername.getText().trim().isEmpty()) err.append("– enter username\n");
        if (editingUser == null && pfPassword.getText().isEmpty()) err.append("– enter the password\n");
        if (tfEmail.getText().trim().isEmpty()) err.append("– enter email\n");
        if (tfFullName.getText().trim().isEmpty()) err.append("– enter a full name\n");
        if (tfPhone.getText().trim().isEmpty()) err.append("– enter phone number\n");
        if (tfAddress.getText().trim().isEmpty()) err.append("– enter an address\n");

        if (err.length() > 0) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.initOwner(dialogStage);
            a.setTitle("Incorrect data");
            a.setHeaderText("Please correct:");
            a.setContentText(err.toString());
            a.showAndWait();
            return;
        }

        try {
            User userToSave;
            if (editingUser == null) {
                userToSave = new User();
                userToSave.setUsername(tfUsername.getText().trim());
                userToSave.setPassword(pfPassword.getText());
                userToSave.setEmail(tfEmail.getText().trim());
                Role customerRole = roleService.findAll().stream()
                        .filter(r -> "Customer".equals(r.getRoleName()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Role 'Customer' not found"));
                userToSave.setRole(customerRole);
            } else {
                userToSave = editingUser;
                userToSave.setUsername(tfUsername.getText().trim());
                if (!pfPassword.getText().isEmpty()) {
                    userToSave.setPassword(pfPassword.getText());
                }
                userToSave.setEmail(tfEmail.getText().trim());
            }

            Customer custToSave;
            if (editingCustomer == null) {
                custToSave = new Customer();
                custToSave.setUser(userToSave);
                custToSave.setFullName(tfFullName.getText().trim());
                custToSave.setPhone(tfPhone.getText().trim());
                custToSave.setAddress(tfAddress.getText().trim());
            } else {
                custToSave = editingCustomer;
                custToSave.setFullName(tfFullName.getText().trim());
                custToSave.setPhone(tfPhone.getText().trim());
                custToSave.setAddress(tfAddress.getText().trim());
            }

            if (editingUser == null) {
                userFacade.createCustomerUser(userToSave, custToSave);
            } else {
                userFacade.updateCustomerUser(userToSave, custToSave);
            }

            okClicked = true;
            dialogStage.close();

        } catch (SQLException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.initOwner(dialogStage);
            a.setTitle("Error when saving");
            a.setHeaderText(null);
            a.setContentText(ex.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
