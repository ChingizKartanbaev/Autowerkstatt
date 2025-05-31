package pti.datenbank.autowerk.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.HelloApplication;

public class MainController {

    @FXML
    private MenuItem logoutMenu;

    @FXML
    private MenuItem exitMenu;

    private AuthService authService;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private void onLogout(ActionEvent event) {
        authService.logout();
        HelloApplication.showLogin();
    }

    @FXML
    private void onExit(ActionEvent event) {
        Stage stage = (Stage) exitMenu.getParentPopup().getOwnerWindow();
        stage.close();
    }
}
