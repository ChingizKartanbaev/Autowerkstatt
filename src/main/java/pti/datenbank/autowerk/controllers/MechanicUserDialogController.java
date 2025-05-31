package pti.datenbank.autowerk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.Mechanic;
import pti.datenbank.autowerk.models.Role;
import pti.datenbank.autowerk.models.User;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.RoleService;
import pti.datenbank.autowerk.services.facade.UserFacade;

import java.sql.SQLException;

public class MechanicUserDialogController {
    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private TextField tfEmail;
    @FXML private TextField tfFullName;
    @FXML private TextField tfSpecialty;

    private Stage dialogStage;
    private UserFacade userFacade;
    private RoleService roleService;
    private AuthService authService;

    private boolean okClicked = false;

    private User editingUser;
    private Mechanic editingMechanic;

    public void setServices(AuthService authService, UserFacade userFacade) {
        this.authService = authService;
        this.userFacade  = userFacade;
        this.roleService = new RoleService(authService);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEditingData(User user, Mechanic mechanic) {
        this.editingUser = user;
        this.editingMechanic = mechanic;
        if (user != null) {
            tfUsername.setText(user.getUsername());
            tfEmail.setText(user.getEmail());
        }
        if (mechanic != null) {
            tfFullName.setText(mechanic.getFullName());
            tfSpecialty.setText(mechanic.getSpeciality());
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        StringBuilder err = new StringBuilder();
        if (tfUsername.getText().trim().isEmpty()) err.append("– введите username\n");
        if (editingUser == null && pfPassword.getText().isEmpty()) err.append("– введите пароль\n");
        if (tfEmail.getText().trim().isEmpty())    err.append("– введите email\n");
        if (tfFullName.getText().trim().isEmpty()) err.append("– введите полное имя\n");
        if (tfSpecialty.getText().trim().isEmpty())err.append("– введите специализацию\n");

        if (err.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(dialogStage);
            alert.setTitle("Неверные данные");
            alert.setHeaderText("Пожалуйста, исправьте:");
            alert.setContentText(err.toString());
            alert.showAndWait();
            return;
        }

        try {
            User userToSave;
            if (editingUser == null) {
                userToSave = new User();
                userToSave.setUsername(tfUsername.getText().trim());
                userToSave.setPassword(pfPassword.getText());
                userToSave.setEmail(tfEmail.getText().trim());
                Role mechRole = roleService.findAll().stream()
                        .filter(r -> "Mechanic".equals(r.getRoleName()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Role 'Mechanic' not found"));
                userToSave.setRole(mechRole);
            } else {
                userToSave = editingUser;
                userToSave.setUsername(tfUsername.getText().trim());
                if (!pfPassword.getText().isEmpty()) {
                    userToSave.setPassword(pfPassword.getText());
                }
                userToSave.setEmail(tfEmail.getText().trim());
            }

            Mechanic mechToSave;
            if (editingMechanic == null) {
                mechToSave = new Mechanic();
                mechToSave.setUser(userToSave);
                mechToSave.setFullName(tfFullName.getText().trim());
                mechToSave.setSpeciality(tfSpecialty.getText().trim());
            } else {
                mechToSave = editingMechanic;
                mechToSave.setFullName(tfFullName.getText().trim());
                mechToSave.setSpeciality(tfSpecialty.getText().trim());
            }

            if (editingUser == null) {
                userFacade.createMechanicUser(userToSave, mechToSave);
            } else {
                userFacade.updateMechanicUser(userToSave, mechToSave);
            }

            okClicked = true;
            dialogStage.close();
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Ошибка при сохранении");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
