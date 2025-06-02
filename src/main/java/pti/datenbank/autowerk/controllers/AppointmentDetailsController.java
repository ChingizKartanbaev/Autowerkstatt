package pti.datenbank.autowerk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.Appointment;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.facade.AppointmentFacade;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class AppointmentDetailsController {

    @FXML private Label lblAppointmentId;
    @FXML private Label lblVehicle;
    @FXML private Label lblMechanic;
    @FXML private Label lblDateTime;
    @FXML private Label lblStatus;
    @FXML private TextArea taServices;

    private Stage dialogStage;
    private AuthService authService;
    private AppointmentFacade appointmentFacade;
    private Appointment appointment;

    public void setServices(AuthService authService) {
        this.authService = authService;
        this.appointmentFacade = new AppointmentFacade(authService);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAppointment(int appointmentId) throws SQLException {
        this.appointment = appointmentFacade.findByIdWithServices(appointmentId);
        fillFields();
    }

    private void fillFields() {
        if (appointment == null) return;

        lblAppointmentId.setText(String.valueOf(appointment.getAppointmentId()));

        if (appointment.getVehicle() != null) {
            String vehDesc = appointment.getVehicle().getMake()
                    + " " + appointment.getVehicle().getModel()
                    + " (" + appointment.getVehicle().getLicensePlate() + ")";
            lblVehicle.setText(vehDesc);
        } else {
            lblVehicle.setText("-");
        }

        if (appointment.getMechanic() != null) {
            lblMechanic.setText(appointment.getMechanic().getFullName());
        } else {
            lblMechanic.setText("-");
        }

        if (appointment.getScheduledAt() != null) {
            String formatted = appointment.getScheduledAt()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            lblDateTime.setText(formatted);
        } else {
            lblDateTime.setText("-");
        }

        lblStatus.setText(appointment.getStatus());

        if (appointment.getServices() != null && !appointment.getServices().isEmpty()) {
            String servicesList = appointment.getServices().stream()
                    .map(srv -> srv.getServiceType().getName())
                    .collect(Collectors.joining("\n"));
            taServices.setText(servicesList);
        } else {
            taServices.setText("(no services)");
        }

        taServices.positionCaret(0);
    }

    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        } else {
            new Alert(Alert.AlertType.ERROR,
                    "Unable to close the window: Stage not set.", ButtonType.OK)
                    .showAndWait();
        }
    }
}
