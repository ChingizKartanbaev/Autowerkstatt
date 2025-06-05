package pti.datenbank.autowerk.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.Mechanic;
import pti.datenbank.autowerk.models.Role;
import pti.datenbank.autowerk.models.User;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.RoleService;
import pti.datenbank.autowerk.services.facade.UserFacade;

import java.sql.SQLException;
import java.util.List;

public class MechanicUserDialogController {

    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private TextField tfEmail;
    @FXML private TextField tfFullName;
    @FXML private ComboBox<String> cbSpeciality;

    private static final List<String> SPECIALITIES = List.of(
            "Engine", "Transmission", "Electrical", "Brakes", "Diagnostics", "General"
    );

    private Stage dialogStage;
    private UserFacade userFacade;
    private RoleService roleService;
    private AuthService authService;

    private boolean okClicked = false;

    private User editingUser;
    private Mechanic editingMechanic;

    @FXML
    private void initialize() {
        cbSpeciality.setItems(FXCollections.observableArrayList(SPECIALITIES));
        cbSpeciality.setEditable(false); // true если разрешить вручную
    }

    public void setServices(AuthService authService, UserFacade userFacade) {
        this.authService = authService;
        this.userFacade = userFacade;
        this.roleService = new RoleService(authService);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
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
            cbSpeciality.setValue(mechanic.getSpeciality());
        }
    }

    @FXML
    private void handleOk() {
        StringBuilder err = new StringBuilder();

        if (tfUsername.getText().trim().isEmpty()) err.append("– Enter username\n");
        if (editingUser == null && pfPassword.getText().isEmpty()) err.append("– Enter password\n");
        if (tfEmail.getText().trim().isEmpty())    err.append("– Enter email\n");
        if (tfFullName.getText().trim().isEmpty()) err.append("– Enter full name\n");
        if (cbSpeciality.getValue() == null || cbSpeciality.getValue().trim().isEmpty()) {
            err.append("– Select speciality\n");
        }

        if (err.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Incorrect Data", "Please correct the following:", err.toString());
            return;
        }

        try {
            User userToSave;
            if (editingUser == null) {
                userToSave = new User();
                userToSave.setUsername(tfUsername.getText().trim());
                userToSave.setPassword(pfPassword.getText());
                userToSave.setEmail(tfEmail.getText().trim());

                Role mechanicRole = roleService.findAll().stream()
                        .filter(r -> "Mechanic".equals(r.getRoleName()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Role 'Mechanic' not found"));
                userToSave.setRole(mechanicRole);
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
                mechToSave.setSpeciality(cbSpeciality.getValue());
            } else {
                mechToSave = editingMechanic;
                mechToSave.setFullName(tfFullName.getText().trim());
                mechToSave.setSpeciality(cbSpeciality.getValue());
            }

            if (editingUser == null) {
                userFacade.createMechanicUser(userToSave, mechToSave);
            } else {
                userFacade.updateMechanicUser(userToSave, mechToSave);
            }

            okClicked = true;
            dialogStage.close();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", null, e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.initOwner(dialogStage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
