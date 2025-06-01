package pti.datenbank.autowerk.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import pti.datenbank.autowerk.HelloApplication;
import pti.datenbank.autowerk.models.Mechanic;
import pti.datenbank.autowerk.models.User;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.MechanicService;
import pti.datenbank.autowerk.services.PartService;
import pti.datenbank.autowerk.services.UserService;
import pti.datenbank.autowerk.services.facade.UserFacade;

import java.io.IOException;

public class MechanicController {

    @FXML private MenuItem logoutMenu;
    @FXML private MenuItem exitMenu;

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

    @FXML
    private void onEditProfile() {
        try {
            User currentUser = authService.getCurrentUser();

            UserService userService = new UserService(authService);
            MechanicService mechanicService = new MechanicService(authService);

            User user = userService.findById(currentUser.getUserId());
            Mechanic mechanic = mechanicService.findByUserId(currentUser.getUserId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pti/datenbank/autowerk/mechanic-user-dialog.fxml"));
            Parent root = loader.load();

            MechanicUserDialogController controller = loader.getController();
            controller.setDialogStage(new Stage());
            controller.setServices(authService, new UserFacade(authService));
            controller.setEditingData(user, mechanic);

            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root));
            controller.setDialogStage(stage);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR, "Could not load profile editor:\n" + e.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    private void onAddPart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pti/datenbank/autowerk/part-dialog.fxml"));
            Parent root = loader.load();

            PartDialogController controller = loader.getController();
            controller.setServices(authService, new PartService(authService));

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);
            dialogStage.setTitle("Add New Part");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR, "Error opening Part Dialog:\n" + e.getMessage());
            a.showAndWait();
        }
    }
}