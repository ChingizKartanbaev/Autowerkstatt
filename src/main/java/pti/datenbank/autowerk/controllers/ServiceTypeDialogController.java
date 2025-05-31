package pti.datenbank.autowerk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.ServiceType;
import pti.datenbank.autowerk.models.User;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.ServiceTypeService;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Контроллер диалога для создания/редактирования ServiceType.
 */
public class ServiceTypeDialogController {
    @FXML private TextField tfName;
    @FXML private TextField tfDescription;
    @FXML private TextField tfBasePrice;

    private Stage dialogStage;
    private boolean okClicked = false;

    private AuthService authService;
    private ServiceTypeService serviceTypeService;

    /** Если null, значит создаём новый ServiceType. Иначе — редактируем этот объект. */
    private ServiceType serviceType;

    /**
     * Вызывается из AdminController после загрузки FXML.
     * @param authService          экземпляр AuthService
     * @param serviceTypeService   экземпляр ServiceTypeService
     */
    public void setServices(AuthService authService, ServiceTypeService serviceTypeService) {
        this.authService = authService;
        this.serviceTypeService = serviceTypeService;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Устанавливает существующий ServiceType для редактирования:
     * заполняет значения в полях формы.
     */
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
        if (serviceType != null) {
            tfName.setText(serviceType.getName());
            tfDescription.setText(serviceType.getDescription());
            tfBasePrice.setText(serviceType.getBasePrice().toString());
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        StringBuilder err = new StringBuilder();
        if (tfName.getText().trim().isEmpty()) {
            err.append("– введите имя услуги\n");
        }
        if (tfDescription.getText().trim().isEmpty()) {
            err.append("– введите описание услуги\n");
        }
        if (tfBasePrice.getText().trim().isEmpty()) {
            err.append("– введите базовую цену\n");
        } else {
            try {
                new BigDecimal(tfBasePrice.getText().trim());
            } catch (NumberFormatException ex) {
                err.append("– базовая цена должна быть числом (например: 123.45)\n");
            }
        }

        if (err.length() > 0) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.initOwner(dialogStage);
            a.setTitle("Неверные данные");
            a.setHeaderText("Пожалуйста, исправьте:");
            a.setContentText(err.toString());
            a.showAndWait();
            return;
        }

        try {
            // Если serviceType == null, создаём новый
            if (serviceType == null) {
                serviceType = new ServiceType();
                // При создании указываем createdBy как текущего пользователя
                User current = authService.getCurrentUser();
                serviceType.setCreatedBy(current);
            }
            // Обновляем или задаём поля name, description, basePrice
            serviceType.setName(tfName.getText().trim());
            serviceType.setDescription(tfDescription.getText().trim());
            serviceType.setBasePrice(new BigDecimal(tfBasePrice.getText().trim()));

            if (serviceType.getServiceTypeId() == 0) {
                // id==0 — это новый объект
                serviceTypeService.create(serviceType);
            } else {
                // Иначе — обновляем существующий
                serviceTypeService.update(serviceType);
            }

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
