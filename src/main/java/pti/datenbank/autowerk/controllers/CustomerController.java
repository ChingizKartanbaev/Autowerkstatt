package pti.datenbank.autowerk.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pti.datenbank.autowerk.HelloApplication;
import pti.datenbank.autowerk.models.*;
import pti.datenbank.autowerk.services.*;
import pti.datenbank.autowerk.services.AppointmentService;
import pti.datenbank.autowerk.services.facade.AppointmentFacade;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    // === SERVICES ===
    private AuthService authService;
    private CustomerService customerService;
    private VehicleService vehicleService;
    private AppointmentService appointmentService;
    private AppointmentFacade appointmentFacade;
    private ServiceTypeService serviceTypeService;
    private AppointmentServiceService appointmentServiceService;
    private PartService partService;

    private MechanicService mechanicService;

    // === Profile tab ===
    @FXML private TextField tfFullName;
    @FXML private TextField tfPhone;
    @FXML private TextField tfAddress;

    // === Vehicles tab ===
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, Integer> colVehId;
    @FXML private TableColumn<Vehicle, String>  colVehMake;
    @FXML private TableColumn<Vehicle, String>  colVehModel;
    @FXML private TableColumn<Vehicle, String>  colVehPlate;
    @FXML private TableColumn<Vehicle, Integer> colVehYear;
    private final ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();

    // === Appointments tab ===

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> colAppId;
    @FXML private TableColumn<Appointment, String>  colAppVeh;
    @FXML private TableColumn<Appointment, String>  colAppMech;
    @FXML private TableColumn<Appointment, String>  colAppDateTime;
    @FXML private TableColumn<Appointment, String>  colAppStatus;

    // === Part tab ===
    @FXML private TableView<Part> partTable;
    @FXML private TableColumn<Part, Integer> colPartId;
    @FXML private TableColumn<Part, String> colPartName;
    @FXML private TableColumn<Part, java.math.BigDecimal> colPartUnitPrice;
    @FXML private TableColumn<Part, Integer> colPartInStockQty;

    // === Mechanic tab ===
    @FXML private TableView<Mechanic> mechanicTable;
    @FXML private TableColumn<Mechanic, Integer> colMechId;
    @FXML private TableColumn<Mechanic, String> colMechName;
    @FXML private TableColumn<Mechanic, String> colMechSpecialty;

    private final ObservableList<Mechanic> mechanicList = FXCollections.observableArrayList();


    private final ObservableList<Part> partList = FXCollections.observableArrayList();

    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initVehicleTable();
        initAppointmentTable();
        initPartTable();
        initMechanicTable();
    }

    public void setAuthService(AuthService authService) {
        this.authService       = authService;
        this.customerService    = new CustomerService(authService);
        this.vehicleService     = new VehicleService(authService);
        this.appointmentService = new AppointmentService(authService);
        this.mechanicService    = new MechanicService(authService);
        this.appointmentService = new AppointmentService(authService);
        this.serviceTypeService = new ServiceTypeService(authService);
        this.appointmentFacade = new AppointmentFacade(authService);
        this.partService = new PartService(authService);
        appointmentServiceService = new AppointmentServiceService(authService);

        loadUserProfile();
        loadUserVehicles();
        loadUserAppointments();
        loadParts();
        loadMechanics();
    }

    // Part Tab
    private void initPartTable() {
        colPartId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPartId()));
        colPartName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
        colPartUnitPrice.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getUnitPrice()));
        colPartInStockQty.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getInStockQty()));
        partTable.setItems(partList);
    }

    private void initMechanicTable() {
        colMechId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getMechanicId()));
        colMechName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getFullName()));
        colMechSpecialty.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getSpeciality()));
        mechanicTable.setItems(mechanicList);
    }

    private void loadParts() {
        try {
            partList.setAll(partService.findAllForCustomer());
        } catch (SQLException e) {
            showError("Failed to load parts: " + e.getMessage());
        }
    }

    private void loadMechanics() {
        try {
            mechanicList.setAll(mechanicService.findAll());
        } catch (SQLException e) {
            showError("Failed to load mechanics:\n" + e.getMessage());
        }
    }

//    @FXML
//    private void onAddPart() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pti/datenbank/autowerk/part-dialog.fxml"));
//            Parent page = loader.load();
//
//            Stage dialogStage = new Stage();
//            dialogStage.setTitle("Add Part");
//            dialogStage.initModality(Modality.WINDOW_MODAL);
//            dialogStage.initOwner(partTable.getScene().getWindow());
//            dialogStage.setScene(new Scene(page));
//
//            PartDialogController dialogCtrl = loader.getController();
//            dialogCtrl.setServices(authService, partService);
//            dialogCtrl.setDialogStage(dialogStage);
//
//            dialogStage.showAndWait();
//
//            if (dialogCtrl.isOkClicked()) {
//                System.out.println("Reloading parts after add...");
//                loadParts();
//            }
//        } catch (Exception e) {
//            showError("Failed to open Part dialog:\n" + e.getMessage());
//        }
//    }

    // === Profile Tab  ====
    private void loadUserProfile() {
        User current = authService.getCurrentUser();
        if (current == null) return;

        try {
            Customer cust = customerService.findByUserId(current.getUserId());
            if (cust != null) {
                tfFullName.setText(cust.getFullName());
                tfPhone.setText(cust.getPhone());
                tfAddress.setText(cust.getAddress());
            } else {
                tfFullName.clear();
                tfPhone.clear();
                tfAddress.clear();
            }
        } catch (SQLException ex) {
            showError("Failed to load profile:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onSaveProfile() {
        String fn   = tfFullName.getText().trim();
        String ph   = tfPhone.getText().trim();
        String addr = tfAddress.getText().trim();

        if (fn.isEmpty() || ph.isEmpty() || addr.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Incorrect data");
            a.setHeaderText("Please fill in all fields.");
            a.showAndWait();
            return;
        }

        try {
            User current = authService.getCurrentUser();
            Customer cust = customerService.findByUserId(current.getUserId());
            if (cust != null) {
                cust.setFullName(fn);
                cust.setPhone(ph);
                cust.setAddress(addr);
                customerService.update(cust);
            } else {
                Customer newCust = new Customer();
                newCust.setUser(current);
                newCust.setFullName(fn);
                newCust.setPhone(ph);
                newCust.setAddress(addr);
                customerService.create(newCust);
            }
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Profile successfully saved.", ButtonType.OK);
            ok.showAndWait();
        } catch (SQLException ex) {
            showError("Error when saving a profile:\n" + ex.getMessage());
        }
    }

    // === Vehicles Tab     ===
    private void initVehicleTable() {
        colVehId.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getVehicleId()));
        colVehMake.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getMake()));
        colVehModel.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getModel()));
        colVehPlate.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getLicensePlate()));
        colVehYear.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getYear()));

        vehicleTable.setItems(vehicleList);
    }

    private void loadUserVehicles() {
        vehicleList.clear();
        try {
            User current = authService.getCurrentUser();
            Customer cust = customerService.findByUserId(current.getUserId());
            if (cust != null) {
                int custId = cust.getCustomerId();
                List<Vehicle> list = vehicleService.findByCustomerId(custId);
                vehicleList.setAll(list);
            }
        } catch (SQLException ex) {
            showError("Failed to load the machine list:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onAddVehicle() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/vehicle-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Vehicle");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(vehicleTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            VehicleDialogController ctrl = loader.getController();
            ctrl.setServices(authService, vehicleService, customerService);
            ctrl.setDialogStage(dialogStage);

            Customer currentCustomer = customerService.findByUserId(authService.getCurrentUser().getUserId());
            ctrl.setFixedCustomer(currentCustomer);

            dialogStage.showAndWait();

            if (ctrl.isOkClicked()) {
                loadUserVehicles();
            }
        } catch (Exception ex) {
            showError("Failed to open the Add Machine dialog box:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onEditVehicle() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert w = new Alert(Alert.AlertType.WARNING,
                    "Please select a machine from the list.", ButtonType.OK);
            w.setTitle("There's no choice");
            w.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/vehicle-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Vehicle");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(vehicleTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            VehicleDialogController ctrl = loader.getController();
            ctrl.setServices(authService, vehicleService, customerService);
            ctrl.setDialogStage(dialogStage);
            ctrl.setVehicle(selected);

            User current = authService.getCurrentUser();
            if ("Customer".equals(current.getRole().getRoleName())) {
                Customer currentCustomer = customerService.findByUserId(current.getUserId());
                ctrl.setFixedCustomer(currentCustomer);
            }

            dialogStage.showAndWait();

            if (ctrl.isOkClicked()) {
                loadUserVehicles();
            }
        } catch (Exception ex) {
            showError("Failed to open the machine editing dialog box:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onDeleteVehicle() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert w = new Alert(Alert.AlertType.WARNING,
                    "Please select a car from the list.", ButtonType.OK);
            w.setTitle("There's no choice");
            w.showAndWait();
            return;
        }

        try {
            boolean hasActive = appointmentService.hasActiveAppointmentsForVehicle(selected.getVehicleId());
            if (hasActive) {
                Alert info = new Alert(Alert.AlertType.INFORMATION,
                        "Cannot delete a car that has active repair records.",
                        ButtonType.OK);
                info.setTitle("Deletion is forbidden");
                info.showAndWait();
                return;
            }
        } catch (SQLException e) {
            showError("Error when checking active records:\n" + e.getMessage());
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "You definitely want to delete the machine \"" +
                        selected.getMake() + " " + selected.getModel() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirm.initOwner(vehicleTable.getScene().getWindow());
        confirm.setTitle("Remove the machine");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.YES) {
            return;
        }

        try {
            vehicleService.delete(selected.getVehicleId());
            loadUserVehicles();
        } catch (SQLException ex) {
            showError("Failed to delete the machine:\n" + ex.getMessage());
        }
    }

//     === Appointments Tab Methods    ===
    private void initAppointmentTable() {
        colAppId.setCellValueFactory(cell ->
                new ReadOnlyObjectWrapper<>(cell.getValue().getAppointmentId()));

        colAppVeh.setCellValueFactory(cell -> {
            Vehicle v = cell.getValue().getVehicle();
            return (v != null)
                    ? new ReadOnlyStringWrapper(v.getMake() + " " + v.getModel())
                    : new ReadOnlyStringWrapper("");
        });

        colAppMech.setCellValueFactory(cell -> {
            Mechanic m = cell.getValue().getMechanic();
            return (m != null)
                    ? new ReadOnlyStringWrapper(m.getFullName())
                    : new ReadOnlyStringWrapper("");
        });

        colAppDateTime.setCellValueFactory(cell -> {
            var dt = cell.getValue().getScheduledAt();
            String formatted = dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return new ReadOnlyStringWrapper(formatted);
        });

        colAppStatus.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getStatus()));

        appointmentTable.setItems(appointmentList);
    }

    private void loadUserAppointments() {
        appointmentList.clear();
        try {
            User current = authService.getCurrentUser();
            Customer cust = customerService.findByUserId(current.getUserId());
            if (cust != null) {
                int custId = cust.getCustomerId();
                List<Appointment> list = appointmentService.findByCustomerId(custId);
                appointmentList.setAll(list);
            }
        } catch (SQLException ex) {
            showError("Failed to load the list of records:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onAddAppointment() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/appointment-dialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("New entry");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(appointmentTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            AppointmentDialogController ctrl = loader.getController();
            ctrl.setServices(authService, vehicleService, mechanicService, appointmentService, serviceTypeService,appointmentFacade);
            ctrl.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (ctrl.isOkClicked()) {
                loadUserAppointments();
            }
        } catch (Exception ex) {
            showError("Failed to open the record creation dialog box:\n" + ex.getMessage());
        }
    }
    @FXML
    private void onEditAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert w = new Alert(Alert.AlertType.WARNING,
                    "Please select an entry from the list.", ButtonType.OK);
            w.setTitle("There's no choice");
            w.showAndWait();
            return;
        }

        if (!"PENDING".equals(selected.getStatus())) {
            Alert info = new Alert(Alert.AlertType.INFORMATION,
                    "Only records with the status “Pending” can be edited", ButtonType.OK);
            info.setTitle("You can't edit");
            info.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/appointment-dialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editing a record");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(appointmentTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            AppointmentDialogController ctrl = loader.getController();
            ctrl.setServices(authService, vehicleService, mechanicService, appointmentService,serviceTypeService, appointmentFacade);
            ctrl.setDialogStage(dialogStage);
            ctrl.setAppointment(selected);

            dialogStage.showAndWait();

            if (ctrl.isOkClicked()) {
                loadUserAppointments();
            }
        } catch (Exception ex) {
            showError("Failed to open the record editing dialog:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onCancelAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert w = new Alert(Alert.AlertType.WARNING,
                    "Please select an entry from the list.", ButtonType.OK);
            w.setTitle("There's no choice");
            w.showAndWait();
            return;
        }

        if (!"PENDING".equals(selected.getStatus())) {
            Alert info = new Alert(Alert.AlertType.INFORMATION,
                    "Only records in “Pending” status can be canceled.", ButtonType.OK);
            info.setTitle("Can't be undone");
            info.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "You definitely want to cancel the recording with ID=" + selected.getAppointmentId() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.initOwner(appointmentTable.getScene().getWindow());
        confirm.setTitle("Cancel the entry");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.YES) {
            return;
        }

        try {
            selected.setStatus("CANCELLED");
            appointmentFacade.updateAppointmentWithServices(selected,
                    appointmentServiceService.findByAppointmentId(selected.getAppointmentId()));
            loadUserAppointments();
        } catch (SQLException ex) {
            showError("Failed to cancel the record:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onViewAppointmentDetails() {
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
                    getClass().getResource("/pti/datenbank/autowerk/appointment-details-dialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Recording details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(appointmentTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            AppointmentDetailsController ctrl = loader.getController();
            ctrl.setServices(authService);
            ctrl.setAppointment(selected.getAppointmentId());
            ctrl.setDialogStage(dialogStage);

            dialogStage.showAndWait();
        } catch (Exception ex) {
            showError("Failed to open record details:\n" + ex.getMessage());
        }
    }

    // === Общие утилитарные методы     ===
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle("Error");
        alert.showAndWait();
    }

    @FXML private void onLogout() {
        authService.logout();
        HelloApplication.showLogin();
    }

    @FXML private void onExit() {
        System.exit(0);
    }
}
