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

    /** Вызывается из AdminController после загрузки FXML */
    public void setServices(AuthService authService, UserFacade userFacade) {
        this.authService   = authService;
        this.userFacade    = userFacade;
        this.roleService   = new RoleService(authService);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        String err = "";
        if (tfUsername.getText().trim().isEmpty()) err += "– введите username\n";
        if (pfPassword.getText().isEmpty())         err += "– введите пароль\n";
        if (tfEmail.getText().trim().isEmpty())    err += "– введите email\n";
        if (tfFullName.getText().trim().isEmpty()) err += "– введите полное имя\n";
        if (tfPhone.getText().trim().isEmpty())    err += "– введите телефон\n";
        if (tfAddress.getText().trim().isEmpty())  err += "– введите адрес\n";

        if (!err.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.initOwner(dialogStage);
            a.setTitle("Неверные данные");
            a.setHeaderText("Пожалуйста, исправьте:");
            a.setContentText(err);
            a.showAndWait();
            return;
        }

        try {
            // 1) Собираем User
            User user = new User();
            user.setUsername(tfUsername.getText().trim());
            user.setPassword(pfPassword.getText());
            user.setEmail(tfEmail.getText().trim());
            // подтягиваем роль Customer
            Role customerRole = roleService.findAll().stream()
                    .filter(r -> "Customer".equals(r.getRoleName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Role 'Customer' not found"));
            user.setRole(customerRole);

            // 2) Собираем Customer-профиль
            Customer profile = new Customer();
            profile.setFullName(tfFullName.getText().trim());
            profile.setPhone(tfPhone.getText().trim());
            profile.setAddress(tfAddress.getText().trim());

            // 3) Вызываем фасад для транзакционного создания
            userFacade.createCustomerUser(user, profile);

            okClicked = true;
            dialogStage.close();
        } catch (SQLException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.initOwner(dialogStage);
            a.setTitle("Ошибка при сохранении");
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
