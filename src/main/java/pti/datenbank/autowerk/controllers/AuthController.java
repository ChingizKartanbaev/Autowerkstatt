package pti.datenbank.autowerk.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.HelloApplication;

public class AuthController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private AuthService authService;

    /**
     * Injects the AuthService.
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Initialize method called by FXMLLoader.
     */
    @FXML
    private void initialize() {
        errorLabel.setText("");
    }

    /**
     * Called when the user clicks the Login button.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        try {
            if (authService.login(username, password)) {
                // On successful login, show the main dashboard
                HelloApplication.showByRole();
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } catch (Exception e) {
            errorLabel.setText("Authentication error. Please try again.");
            e.printStackTrace();
        }
    }

}

