package pti.datenbank.autowerk.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pti.datenbank.autowerk.HelloApplication;
import pti.datenbank.autowerk.models.Appointment;
import pti.datenbank.autowerk.models.Part;
import pti.datenbank.autowerk.models.Customer;
import pti.datenbank.autowerk.models.Vehicle;
import pti.datenbank.autowerk.models.Mechanic;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.CustomerService;
import pti.datenbank.autowerk.services.MechanicService;
import pti.datenbank.autowerk.services.PartService;
import pti.datenbank.autowerk.services.AppointmentPartService;
import pti.datenbank.autowerk.services.facade.AppointmentFacade;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MechanicController {

    // === SERVICES ===
    private PartService partService;
    private AppointmentFacade appointmentFacade;
    private MechanicService mechanicService;
    private CustomerService customerService;
    private AppointmentPartService appointmentPartService;

    private AuthService authService;
    private MechanicProfileController profileController;

    // === PROFILE TAB ===
    @FXML private Tab tabProfile;
    @FXML private VBox profileRoot;

    // ==== PART TAB ====
    @FXML private TableView<Part> partTable;
    @FXML private TableColumn<Part, Integer> colPartId;
    @FXML private TableColumn<Part, String> colPartName;
    @FXML private TableColumn<Part, String> colPartManufacturer;
    @FXML private TableColumn<Part, BigDecimal> colPartUnitPrice;
    @FXML private TableColumn<Part, Integer> colPartInStockQty;

    // ==== APPOINTMENTS TAB ====
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> colAppId;
    @FXML private TableColumn<Appointment, String>  colAppCustomer;
    @FXML private TableColumn<Appointment, String>  colAppVehicle;
    @FXML private TableColumn<Appointment, String>  colAppDateTime;
    @FXML private TableColumn<Appointment, String>  colAppStatus;

    @FXML private MenuItem logoutMenu;
    @FXML private MenuItem exitMenu;

    private final ObservableList<Part> partList = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        this.mechanicService = new MechanicService(authService);
        this.customerService = new CustomerService(authService);
        this.partService = new PartService(authService);
        this.appointmentFacade = new AppointmentFacade(authService);
        this.appointmentPartService = new AppointmentPartService(authService);
        loadParts();
        loadMyAppointments();

        if (profileController != null) {
            profileController.setServices(authService);
            profileController.loadProfile();
        } else {
            showError("Profile controller is not loaded.");
        }
    }

    @FXML
    private void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pti/datenbank/autowerk/mechanic-profile.fxml"));
            Parent profileContent = loader.load();
            profileController = loader.getController();
            profileRoot.getChildren().setAll(profileContent);
        } catch (Exception e) {
            showError("Failed to load profile view:\n" + e.getMessage());
        }
        initPartTable();
        initAppointmentTable();
    }

    private void initAppointmentTable() {
        colAppId.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getAppointmentId()));

        colAppCustomer.setCellValueFactory(cell -> {
            Customer c = cell.getValue().getCustomer();
            String text = (c != null ? c.getFullName() : "-");
            return new ReadOnlyStringWrapper(text);
        });

        colAppVehicle.setCellValueFactory(cell -> {
            Vehicle v = cell.getValue().getVehicle();
            String text = (v != null
                    ? v.getMake() + " " + v.getModel() + " (" + v.getLicensePlate() + ")"
                    : "-");
            return new ReadOnlyStringWrapper(text);
        });

        colAppDateTime.setCellValueFactory(cell -> {
            if (cell.getValue().getScheduledAt() != null) {
                String formatted = cell.getValue().getScheduledAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                return new ReadOnlyStringWrapper(formatted);
            } else {
                return new ReadOnlyStringWrapper("-");
            }
        });

        colAppStatus.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStatus()));

        appointmentTable.setItems(appointmentList);
    }

    private void initPartTable() {
        colPartId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPartId()));
        colPartName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
        colPartManufacturer.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getManufacturer()));
        colPartUnitPrice.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getUnitPrice()));
        colPartInStockQty.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getInStockQty()));
        partTable.setItems(partList);
    }

    // ========== APPOINTMENTS TAB ==========

    private void loadMyAppointments() {
        appointmentList.clear();
        try {
            Mechanic mech = mechanicService.findByUserId(authService.getCurrentUser().getUserId());
            if (mech != null) {
                int mechId = mech.getMechanicId();

                List<Appointment> list = appointmentFacade.findByMechanicId(mechId);

                List<Appointment> activeAppointments = list.stream()
                        .filter(ap -> !"CANCELLED".equalsIgnoreCase(ap.getStatus()))
                        .toList();

                appointmentList.setAll(activeAppointments);
            }
        } catch (SQLException ex) {
            showError("Failed to load your assigned records:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onAddPartToAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert w = new Alert(Alert.AlertType.WARNING,
                    "Please select an entry from the list.", ButtonType.OK);
            w.setTitle("There's no choice");
            w.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/appointment-part-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add spare part to entry ID=" + selected.getAppointmentId());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(appointmentTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            AppointmentPartDialogController ctrl = loader.getController();
            ctrl.setDialogStage(dialogStage);
            ctrl.setServices(authService, partService, selected, appointmentPartService);

            dialogStage.showAndWait();

            if (ctrl.isOkClicked()) {
                loadMyAppointments();
                loadParts();
            }
        } catch (Exception ex) {
            showError("Failed to open the Add Part dialog box:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onShowAppointmentDetails() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an appointment.", ButtonType.OK).showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pti/datenbank/autowerk/appointment-details-dialog.fxml"));
            Parent page = loader.load();

            AppointmentDetailsController controller = loader.getController();
            controller.setDialogStage(new Stage());
            controller.setServices(authService);
            controller.setAppointment(selected.getAppointmentId());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Appointment Details - ID=" + selected.getAppointmentId());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(appointmentTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Failed to open details window:\n" + ex.getMessage());
        }
    }



    @FXML
    private void onLogout(ActionEvent event) {
        authService.logout();
        HelloApplication.showLogin();
    }

    @FXML
    private void onExit(ActionEvent event) {
        Stage stage = (Stage) exitMenu.getParentPopup().getOwnerWindow();
        stage.close();
    }

    @FXML
    private void onChangeStatus() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Выберите запись из таблицы.", ButtonType.OK).showAndWait();
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(selected.getStatus(),
                FXCollections.observableArrayList("PENDING", "IN_PROGRESS", "DONE"));
        dialog.setTitle("Изменение статуса");
        dialog.setHeaderText("Изменить статус записи ID=" + selected.getAppointmentId());
        dialog.setContentText("Новый статус:");

        dialog.showAndWait().ifPresent(newStatus -> {
            if (!newStatus.equals(selected.getStatus())) {
                try {
                    selected.setStatus(newStatus);
                    appointmentFacade.updateAppointmentWithServices(selected, selected.getServices());
                    loadMyAppointments();
                } catch (SQLException e) {
                    showError("Не удалось обновить статус:\n" + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onAddPart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pti/datenbank/autowerk/part-dialog.fxml"));
            Parent root = loader.load();

            PartDialogController controller = loader.getController();
            controller.setServices(authService, partService);

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);
            dialogStage.setTitle("Add New Part");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                System.out.println("[DEBUG] okClicked is TRUE — calling loadParts()");
                loadParts();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening Part Dialog:\n" + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Error");
        a.setContentText(msg);
        a.showAndWait();
    }

    private void loadParts() {
        try {
            partList.setAll(partService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to load parts:\n" + e.getMessage());
        }
    }
}