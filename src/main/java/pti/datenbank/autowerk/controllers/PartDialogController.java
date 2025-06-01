package pti.datenbank.autowerk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.Part;
import pti.datenbank.autowerk.models.User;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.PartService;

import java.math.BigDecimal;
import java.sql.SQLException;

public class PartDialogController {

    @FXML private TextField tfName;
    @FXML private TextField tfManufacturer;
    @FXML private TextField tfUnitPrice;
    @FXML private TextField tfInStockQty;

    private Stage dialogStage;
    private boolean okClicked = false;

    private AuthService authService;
    private PartService partService;

    private Part part;

    public void setServices(AuthService authService, PartService partService) {
        this.authService = authService;
        this.partService = partService;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPart(Part part) {
        this.part = part;
        if (part != null) {
            tfName.setText(part.getName());
            tfManufacturer.setText(part.getManufacturer());
            tfUnitPrice.setText(part.getUnitPrice().toString());
            tfInStockQty.setText(String.valueOf(part.getInStockQty()));
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        StringBuilder err = new StringBuilder();

        if (tfName.getText().trim().isEmpty()) {
            err.append("– enter a part name\n");
        }
        if (tfManufacturer.getText().trim().isEmpty()) {
            err.append("– enter manufacturer\n");
        }
        if (tfUnitPrice.getText().trim().isEmpty()) {
            err.append("– enter the part price\n");
        } else {
            try {
                new BigDecimal(tfUnitPrice.getText().trim());
            } catch (NumberFormatException ex) {
                err.append("– the “Unit Price” field must be a number (for example: 45.99)\n");
            }
        }
        if (tfInStockQty.getText().trim().isEmpty()) {
            err.append("– enter stock quantity\n");
        } else {
            try {
                Integer.parseInt(tfInStockQty.getText().trim());
            } catch (NumberFormatException ex) {
                err.append("– the “In-Stock Quantity” field must be a whole number\n");
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
            if (part == null) {
                part = new Part();
                User current = authService.getCurrentUser();
                part.setCreatedBy(current);
            }
            part.setName(tfName.getText().trim());
            part.setManufacturer(tfManufacturer.getText().trim());
            part.setUnitPrice(new BigDecimal(tfUnitPrice.getText().trim()));
            part.setInStockQty(Integer.parseInt(tfInStockQty.getText().trim()));

            if (part.getPartId() == 0) {
                System.out.println("Creating part: " + part.getName() +
                        " by user ID: " + authService.getCurrentUser().getUserId());
                partService.create(part);
            } else {
                partService.update(part);
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
