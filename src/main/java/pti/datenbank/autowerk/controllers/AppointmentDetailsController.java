package pti.datenbank.autowerk.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.Appointment;
import pti.datenbank.autowerk.models.AppointmentPart;
import pti.datenbank.autowerk.services.AppointmentPartService;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.facade.AppointmentFacade;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentDetailsController {

    @FXML private Label lblAppointmentId;
    @FXML private Label lblVehicle;
    @FXML private Label lblMechanic;
    @FXML private Label lblDateTime;
    @FXML private Label lblStatus;
    @FXML private TextArea taServices;
    @FXML private Label lblTotalPrice;
    @FXML private TextArea taParts;

    private Stage dialogStage;
    private AuthService authService;
    private AppointmentFacade appointmentFacade;
    private Appointment appointment;
    private AppointmentPartService appointmentPartService;


    public void setServices(AuthService authService) {
        this.authService = authService;
        this.appointmentFacade = new AppointmentFacade(authService);
        this.appointmentPartService = new AppointmentPartService(authService);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAppointment(int appointmentId) throws SQLException {
        this.appointment = appointmentFacade.findByIdWithServices(appointmentId);
        fillFields();
        loadParts();
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

        try {
            // Общая стоимость услуг
            double serviceTotal = 0.0;
            if (appointment.getServices() != null) {
                serviceTotal = appointment.getServices().stream()
                        .map(s -> s.getServiceType().getBasePrice())
                        .mapToDouble(BigDecimal::doubleValue)
                        .sum();
            }

            // Общая стоимость деталей
            double partsTotal = 0.0;
            var appointmentPartService = new pti.datenbank.autowerk.services.AppointmentPartService(authService);
            var parts = appointmentPartService.findByAppointmentId(appointment.getAppointmentId());

            partsTotal = parts.stream()
                    .mapToDouble(p -> p.getPart().getUnitPrice().doubleValue() * p.getQuantity())
                    .sum();

            double total = serviceTotal + partsTotal;
            lblTotalPrice.setText(String.format("%.2f €", total));

        } catch (Exception e) {
            lblTotalPrice.setText("Ошибка расчёта");
            e.printStackTrace(); // или showError, если хочешь alert
        }
    }

    private void loadParts() {
        try {
            List<AppointmentPart> parts = appointmentPartService.findByAppointmentId(appointment.getAppointmentId());
            if (parts.isEmpty()) {
                taParts.setText("(no parts used)");
            } else {
                String list = parts.stream()
                        .map(ap -> ap.getPart().getName() + " × " + ap.getQuantity())
                        .collect(Collectors.joining("\n"));
                taParts.setText(list);
            }
            taParts.positionCaret(0);
        } catch (SQLException ex) {
            taParts.setText("Failed to load parts:\n" + ex.getMessage());
        }
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
