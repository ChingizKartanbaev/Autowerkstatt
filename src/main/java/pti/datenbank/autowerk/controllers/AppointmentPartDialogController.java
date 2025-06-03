package pti.datenbank.autowerk.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.Appointment;
import pti.datenbank.autowerk.models.AppointmentPart;
import pti.datenbank.autowerk.models.Part;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.PartService;
import pti.datenbank.autowerk.services.AppointmentPartService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AppointmentPartDialogController implements Initializable {

    @FXML private ComboBox<Part> cbPart;
    @FXML private TextField tfQuantity;
    @FXML private Button btnOk;
    @FXML private Button btnCancel;

    private Stage dialogStage;
    private AuthService authService;
    private PartService partService;
    private Appointment appointment;
    private AppointmentPartService appointmentPartService;

    private final ObservableList<Part> partList = FXCollections.observableArrayList();
    private boolean okClicked = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbPart.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Part item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getManufacturer() + ")");
                }
            }
        });
        cbPart.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Part item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getManufacturer() + ")");
                }
            }
        });
    }

    public void setServices(AuthService authService,
                            PartService partService,
                            Appointment appointment,
                            AppointmentPartService appointmentPartService)
    {
        this.authService = authService;
        this.partService = partService;
        this.appointment = appointment;
        this.appointmentPartService = appointmentPartService;

        loadPartsIntoComboBox();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    private void loadPartsIntoComboBox() {
        partList.clear();
        try {
            List<Part> allParts = partService.findAll();
            partList.setAll(allParts);
            cbPart.setItems(partList);
        } catch (SQLException ex) {
            showError("Failed to load parts list:\n" + ex.getMessage());
        }
    }

    @FXML
    private void handleOk() {
        StringBuilder err = new StringBuilder();

        Part selectedPart = cbPart.getValue();
        String qtyText = tfQuantity.getText().trim();
        int qty = 0;

        if (selectedPart == null) {
            err.append("– select a part\n");
        }
        if (qtyText.isEmpty()) {
            err.append("– enter the quantity\n");
        } else {
            try {
                qty = Integer.parseInt(qtyText);
                if (qty <= 0) {
                    err.append("– the number must be a positive number\n");
                } else if (selectedPart != null && qty > selectedPart.getInStockQty()) {
                    err.append("– not enough parts in stock (available: ")
                            .append(selectedPart.getInStockQty()).append(")\n");
                }
            } catch (NumberFormatException e) {
                err.append("– Incorrect quantity format (enter integer)\n");
            }
        }

        if (err.length() > 0) {
            showError(err.toString());
            return;
        }

        try {
            int appointmentId = appointment.getAppointmentId();
            int partId = selectedPart.getPartId();

            // Проверяем — уже есть такая пара?
            AppointmentPart existing = appointmentPartService.findOne(appointmentId, partId);

            if (existing != null) {
                // Если да — обновляем количество
                int newQty = existing.getQuantity() + qty;
                appointmentPartService.updateQuantity(appointmentId, partId, newQty);
            } else {
                // Иначе — создаём новую запись
                AppointmentPart ap = new AppointmentPart();
                ap.setAppointment(appointment);
                ap.setPart(selectedPart);
                ap.setQuantity(qty);
                appointmentPartService.create(ap);
            }

            // Обновим склад
            selectedPart.setInStockQty(selectedPart.getInStockQty() - qty);
            partService.update(selectedPart);

            okClicked = true;
            dialogStage.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Error when saving a part:\n" + ex.getMessage());
        }
    }


    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.initOwner(dialogStage);
        a.setTitle("Error");
        a.showAndWait();
    }
}