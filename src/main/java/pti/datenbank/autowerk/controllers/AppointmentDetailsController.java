package pti.datenbank.autowerk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.Appointment;

import java.time.format.DateTimeFormatter;

public class AppointmentDetailsController {

    @FXML private Label lblAppointmentId;
    @FXML private Label lblVehicle;
    @FXML private Label lblMechanic;
    @FXML private Label lblDateTime;
    @FXML private Label lblStatus;
    @FXML private Label lblDescription;

    private Stage dialogStage;
    private Appointment appointment;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        fillFields();
    }

    private void fillFields() {
        if (appointment == null) return;

        lblAppointmentId.setText(String.valueOf(appointment.getAppointmentId()));

        var veh = appointment.getVehicle();
        if (veh != null) {
            lblVehicle.setText(veh.getMake() + " " + veh.getModel() +
                    " (" + veh.getLicensePlate() + ")");
        } else {
            lblVehicle.setText("-");
        }

        var mech = appointment.getMechanic();
        lblMechanic.setText(mech != null ? mech.getFullName() : "-");

        var dt = appointment.getScheduledAt();
        if (dt != null) {
            lblDateTime.setText(dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        } else {
            lblDateTime.setText("-");
        }

        lblStatus.setText(appointment.getStatus() != null ? appointment.getStatus() : "-");
    }

    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        } else {
            new Alert(Alert.AlertType.ERROR,
                    "Невозможно закрыть окно: Stage не задан.", ButtonType.OK)
                    .showAndWait();
        }
    }
}
