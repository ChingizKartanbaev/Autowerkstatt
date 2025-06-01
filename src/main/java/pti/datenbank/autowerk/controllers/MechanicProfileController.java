package pti.datenbank.autowerk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pti.datenbank.autowerk.models.Mechanic;
import pti.datenbank.autowerk.models.User;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.MechanicService;
import pti.datenbank.autowerk.services.UserService;

public class MechanicProfileController {

    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private TextField tfEmail;
    @FXML private TextField tfFullName;
    @FXML private TextField tfSpecialty;
    @FXML private TextField tfPhone;

    private AuthService authService;
    private UserService userService;
    private MechanicService mechanicService;
    private User user;
    private Mechanic mechanic;

    public void setServices(AuthService authService) {
        this.authService = authService;
        this.userService  = new UserService(authService);
        this.mechanicService = new MechanicService(authService);
    }

    public void loadProfile() {
        try {
            user = authService.getCurrentUser();
            mechanic = mechanicService.findByUserId(user.getUserId());

            tfUsername.setText(user.getUsername());
            tfEmail.setText(user.getEmail());
            tfFullName.setText(mechanic.getFullName());
            tfSpecialty.setText(mechanic.getSpeciality());

        } catch (Exception e) {
            showError("Failed to load mechanic profile:\n" + e.getMessage());
        }
    }

    @FXML
    private void onSaveProfile() {
        try {
            user.setUsername(tfUsername.getText().trim());
            if (!pfPassword.getText().trim().isEmpty()) {
                user.setPassword(pfPassword.getText().trim());
            }
            user.setEmail(tfEmail.getText().trim());

            mechanic.setFullName(tfFullName.getText().trim());
            mechanic.setSpeciality(tfSpecialty.getText().trim());

            userService.update(user);
            mechanicService.update(mechanic);

            showInfo("Profile is successfully updated.");
        } catch (Exception e) {
            showError("Error:\n" + e.getMessage());
        }
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Message");
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Error");
        a.setContentText(msg);
        a.showAndWait();
    }
}