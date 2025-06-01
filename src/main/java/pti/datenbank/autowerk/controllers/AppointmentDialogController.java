package pti.datenbank.autowerk.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.*;
import pti.datenbank.autowerk.services.*;
import pti.datenbank.autowerk.services.AppointmentService;
import pti.datenbank.autowerk.services.facade.AppointmentFacade;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AppointmentDialogController implements Initializable {

    @FXML private ComboBox<Vehicle> cbVehicle;
    @FXML private ComboBox<Mechanic> cbMechanic;
    @FXML private DatePicker dpDate;
    @FXML private TextField tfTime;
    @FXML private ComboBox<ServiceType> cbServiceType;
    @FXML private Button btnOk;
    @FXML private Button btnCancel;

    private AuthService authService;
    private VehicleService vehicleService;
    private MechanicService mechanicService;
    private ServiceTypeService serviceTypeService;
    private AppointmentService appointmentService;
    private AppointmentFacade appointmentFacade;

    private Stage dialogStage;
    private boolean okClicked = false;
    private Appointment appointment;

    private final ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
    private final ObservableList<Mechanic> mechanicList = FXCollections.observableArrayList();
    private final ObservableList<ServiceType> serviceTypeList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Настроим отображение текста в ComboBox для Vehicle и Mechanic
        cbVehicle.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Vehicle item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null
                        : item.getMake() + " " + item.getModel() + " (" + item.getLicensePlate() + ")");
            }
        });
        cbVehicle.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Vehicle item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null
                        : item.getMake() + " " + item.getModel() + " (" + item.getLicensePlate() + ")");
            }
        });

        cbMechanic.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Mechanic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName());
            }
        });
        cbMechanic.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Mechanic item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName());
            }
        });

        cbServiceType.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ServiceType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        cbServiceType.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ServiceType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
    }

    public void setServices(AuthService authService,
                            VehicleService vehicleService,
                            MechanicService mechanicService,
                            AppointmentService appointmentService,
                            ServiceTypeService serviceTypeService,
                            AppointmentFacade appointmentFacade) {
        this.authService        = authService;
        this.vehicleService     = vehicleService;
        this.mechanicService    = mechanicService;
        this.serviceTypeService  = serviceTypeService   ;
        this.appointmentService = appointmentService;
        this.appointmentFacade  = appointmentFacade;

        loadMechanicsIntoComboBox();
        loadVehiclesIntoComboBox();
        loadServiceTypesIntoComboBox();
    }


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;

        // Заполняем поля:
        if (appointment.getVehicle() != null) {
            cbVehicle.getSelectionModel().select(appointment.getVehicle());
        }
        if (appointment.getMechanic() != null) {
            cbMechanic.getSelectionModel().select(appointment.getMechanic());
        }
        LocalDateTime sched = appointment.getScheduledAt();
        if (sched != null) {
            dpDate.setValue(sched.toLocalDate());
            tfTime.setText(sched.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }


    private void loadMechanicsIntoComboBox() {
        mechanicList.clear();
        try {
            List<Mechanic> all = mechanicService.findAll();
            mechanicList.setAll(all);
            cbMechanic.setItems(mechanicList);
        } catch (SQLException ex) {
            showError("Не удалось загрузить список механиков:\n" + ex.getMessage());
        }
    }

    private void loadVehiclesIntoComboBox() {
        vehicleList.clear();
        try {
            String roleName = authService.getCurrentUser().getRole().getRoleName();
            if ("Customer".equals(roleName)) {
                int userId = authService.getCurrentUser().getUserId();
                Customer cust = new CustomerService(authService).findByUserId(userId);
                if (cust != null) {
                    vehicleList.setAll(vehicleService.findByCustomerId(cust.getCustomerId()));
                }
            } else {
                vehicleList.setAll(vehicleService.findAll());
            }
            cbVehicle.setItems(vehicleList);
        } catch (SQLException ex) {
            showError("Не удалось загрузить список машин:\n" + ex.getMessage());
        }
    }

    private void loadServiceTypesIntoComboBox() {
        serviceTypeList.clear();
        try {
            List<ServiceType> all = serviceTypeService.findAll();
            serviceTypeList.setAll(all);
            cbServiceType.setItems(serviceTypeList);
        } catch (SQLException ex) {
            showError("Не удалось загрузить список типов сервисных услуг:\n" + ex.getMessage());
        }
    }

    @FXML
    private void handleOk() {
        StringBuilder err = new StringBuilder();

        Vehicle selVeh = cbVehicle.getValue();
        Mechanic selMech = cbMechanic.getValue();
        LocalDate date = dpDate.getValue();
        ServiceType selServType = cbServiceType.getValue();
        String timeText = tfTime.getText().trim();

        if (selVeh == null) {
            err.append("– выберите машину\n");
        }
        if (selMech == null) {
            err.append("– выберите механика\n");
        }
        if (date == null) {
            err.append("– выберите дату\n");
        }
        if (timeText.isEmpty()) {
            err.append("– введите время (HH:mm)\n");
        } else {
            try {
                LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e) {
                err.append("– неверный формат времени (ожидается HH:mm)\n");
            }
        }

        if (err.length() > 0) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.initOwner(dialogStage);
            warning.setTitle("Некорректные данные");
            warning.setHeaderText("Пожалуйста, исправьте:");
            warning.setContentText(err.toString());
            warning.showAndWait();
            return;
        }

        LocalTime time = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime scheduled = LocalDateTime.of(date, time);

        try {
            if (appointment == null) {
                Appointment newApp = new Appointment();
                newApp.setCustomer( new CustomerService(authService)
                        .findByUserId(authService.getCurrentUser().getUserId()) );
                newApp.setVehicle(selVeh);
                newApp.setMechanic(selMech);
                newApp.setScheduledAt(scheduled);
                newApp.setStatus("PENDING");

                List<ServiceType> servicesForAppointment = List.of(selServType);
                appointmentFacade.createAppointmentWithServices(newApp, servicesForAppointment);
            } else {
                appointment.setVehicle(selVeh);
                appointment.setMechanic(selMech);
                appointment.setScheduledAt(scheduled);

                pti.datenbank.autowerk.models.AppointmentService appServ = new pti.datenbank.autowerk.models.AppointmentService();
                appServ.setAppointment(appointment);
                appServ.setServiceType(selServType);

                appointmentFacade.updateAppointmentWithServices(appointment, List.of(appServ));
            }

            okClicked = true;
            dialogStage.close();
        } catch (SQLException ex) {
            showError("Ошибка при сохранении записи:\n" + ex.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.initOwner(dialogStage);
        alert.setTitle("Ошибка");
        alert.showAndWait();
    }
}
