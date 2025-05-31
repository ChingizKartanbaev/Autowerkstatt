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

public class ServiceTypeDialogController {
    @FXML private TextField tfName;
    @FXML private TextField tfDescription;
    @FXML private TextField tfBasePrice;

    private Stage dialogStage;
    private boolean okClicked = false;

    private AuthService authService;
    private ServiceTypeService serviceTypeService;

    private ServiceType serviceType;

    public void setServices(AuthService authService, ServiceTypeService serviceTypeService) {
        this.authService = authService;
        this.serviceTypeService = serviceTypeService;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

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
            err.append("– enter a service name\n");
        }
        if (tfDescription.getText().trim().isEmpty()) {
            err.append("– enter a description of the service\n");
        }
        if (tfBasePrice.getText().trim().isEmpty()) {
            err.append("– enter a base price\n");
        } else {
            try {
                new BigDecimal(tfBasePrice.getText().trim());
            } catch (NumberFormatException ex) {
                err.append("– the base price must be a number (for example: 123.45)\n");
            }
        }

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
            if (serviceType == null) {
                serviceType = new ServiceType();
                User current = authService.getCurrentUser();
                serviceType.setCreatedBy(current);
            }
            serviceType.setName(tfName.getText().trim());
            serviceType.setDescription(tfDescription.getText().trim());
            serviceType.setBasePrice(new BigDecimal(tfBasePrice.getText().trim()));

            if (serviceType.getServiceTypeId() == 0) {
                serviceTypeService.create(serviceType);
            } else {
                serviceTypeService.update(serviceType);
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
