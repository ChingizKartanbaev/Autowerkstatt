package pti.datenbank.autowerk.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.Appointment;
import pti.datenbank.autowerk.models.Customer;
import pti.datenbank.autowerk.models.Vehicle;
import pti.datenbank.autowerk.services.CustomerService;
import pti.datenbank.autowerk.services.MechanicService;
import pti.datenbank.autowerk.services.VehicleService;
import pti.datenbank.autowerk.services.facade.AppointmentFacade;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class MechanicAppointmentDialogController implements Initializable {

    @FXML private Label    lblAppId;
    @FXML private Label    lblCustomer;
    @FXML private Label    lblVehicle;
    @FXML private Label    lblDateTime;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextArea taNotes;

    private Stage dialogStage;
    private Appointment appointment;
    private AppointmentFacade appointmentFacade;
    private MechanicService mechanicService;
    private CustomerService customerService;
    private VehicleService vehicleService;

    private final ObservableList<String> statusOptions =
            FXCollections.observableArrayList("PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbStatus.setItems(statusOptions);
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setServices(AppointmentFacade appointmentFacade,
                            MechanicService mechanicService,
                            CustomerService customerService,
                            VehicleService vehicleService) {
        this.appointmentFacade = appointmentFacade;
        this.mechanicService   = mechanicService;
        this.customerService   = customerService;
        this.vehicleService    = vehicleService;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        fillFieldsFromAppointment();
    }

    private void fillFieldsFromAppointment() {
        if (appointment == null) return;

        lblAppId.setText(String.valueOf(appointment.getAppointmentId()));

        Customer c = appointment.getCustomer();
        if (c != null) {
            lblCustomer.setText(c.getFullName());
        } else {
            lblCustomer.setText("-");
        }

        Vehicle v = appointment.getVehicle();
        if (v != null) {
            String vehicleDesc = v.getMake() + " " + v.getModel() + " (" + v.getLicensePlate() + ")";
            lblVehicle.setText(vehicleDesc);
        } else {
            lblVehicle.setText("-");
        }

        if (appointment.getScheduledAt() != null) {
            String dt = appointment.getScheduledAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            lblDateTime.setText(dt);
        } else {
            lblDateTime.setText("-");
        }

        String currentStatus = appointment.getStatus();
        if (currentStatus != null && statusOptions.contains(currentStatus)) {
            cbStatus.getSelectionModel().select(currentStatus);
        } else {
            cbStatus.getSelectionModel().select("PENDING");
        }
    }

    @FXML
    private void handleSave() {
        if (appointment == null) return;

        String newStatus = cbStatus.getValue();
        appointment.setStatus(newStatus);

        try {
            appointmentFacade.updateAppointmentWithServices(appointment, List.of());
        } catch (SQLException ex) {
            showError("Error when saving changes:\n" + ex.getMessage());
            return;
        }

        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.initOwner(dialogStage);
        alert.setTitle("Error");
        alert.showAndWait();
    }
}