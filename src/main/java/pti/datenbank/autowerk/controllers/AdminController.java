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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pti.datenbank.autowerk.HelloApplication;
import pti.datenbank.autowerk.models.Customer;
import pti.datenbank.autowerk.models.Vehicle;
import pti.datenbank.autowerk.models.Mechanic;
import pti.datenbank.autowerk.models.Part;
import pti.datenbank.autowerk.models.ServiceType;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.CustomerService;
import pti.datenbank.autowerk.services.MechanicService;
import pti.datenbank.autowerk.services.PartService;
import pti.datenbank.autowerk.services.VehicleService;
import pti.datenbank.autowerk.services.ServiceTypeService;
import pti.datenbank.autowerk.services.facade.UserFacade;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    // ==== Services ==== //
    private AuthService authService;
    private CustomerService customerService;
    private MechanicService mechanicService;
    private ServiceTypeService serviceTypeService;
    private PartService partService;
    private VehicleService vehicleService;
    private UserFacade userFacade;

    // ==== Customers tab ==== //
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colCustomerId;
    @FXML private TableColumn<Customer, String> colCustomerFullName;
    @FXML private TableColumn<Customer, String> colCustomerAddress;
    @FXML private TableColumn<Customer, String> colCustomerPhone;

    // ==== Mechanics tab ==== //
    @FXML private TableView<Mechanic> mechanicTable;
    @FXML private TableColumn<Mechanic, Integer> colMechanicId;
    @FXML private TableColumn<Mechanic, String> colMechanicFullName;
    @FXML private TableColumn<Mechanic, String> colMechanicSpeciality;

    // ==== Service Types tab ==== //
    @FXML private TableView<ServiceType> serviceTypeTable;
    @FXML private TableColumn<ServiceType, Integer> colServiceTypeId;
    @FXML private TableColumn<ServiceType, String> colServiceTypeName;
    @FXML private TableColumn<ServiceType, String> colServiceTypeDescription;
    @FXML private TableColumn<ServiceType, BigDecimal> colServiceTypePrice;

    // ==== Parts tab ==== //
    @FXML private TableView<Part> partTable;
    @FXML private TableColumn<Part, Integer> colPartId;
    @FXML private TableColumn<Part, String> colPartName;
    @FXML private TableColumn<Part, java.math.BigDecimal> colPartUnitPrice;
    @FXML private TableColumn<Part, Integer> colPartInStockQty;

    // ==== Vehicles tab ==== //
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, Integer> colVehicleId;
    @FXML private TableColumn<Vehicle, String> colVehicleCustomer;
    @FXML private TableColumn<Vehicle, String> colVehicleMake;
    @FXML private TableColumn<Vehicle, String> colVehicleModel;
    @FXML private TableColumn<Vehicle, String> colVehiclePlate;
    @FXML private TableColumn<Vehicle, Integer> colVehicleYear;

    // ==== Data lists ==== //
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private final ObservableList<Mechanic> mechanicList = FXCollections.observableArrayList();
    private final ObservableList<ServiceType> serviceTypeList = FXCollections.observableArrayList();
    private final ObservableList<Part> partList = FXCollections.observableArrayList();
    private final ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCustomerTable();
        initMechanicTable();
        initServiceTypeTable();
        initPartTable();
        initVehicleTable();
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        this.customerService = new CustomerService(authService);
        this.mechanicService = new MechanicService(authService);
        this.serviceTypeService = new ServiceTypeService(authService);
        this.partService = new PartService(authService);
        this.vehicleService  = new VehicleService(authService);
        this.userFacade = new UserFacade(authService);
        try {
            loadAllData();
        } catch (SQLException e) {
            showError("Error loading data: " + e.getMessage());
        }
    }

    private void initCustomerTable() {
        colCustomerId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getCustomerId()));
        colCustomerFullName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getFullName()));
        colCustomerAddress.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getAddress()));
        colCustomerPhone.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getPhone()));
        customerTable.setItems(customerList);
    }

    private void initMechanicTable() {
        colMechanicId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getMechanicId()));
        colMechanicFullName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getFullName()));
        colMechanicSpeciality.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getSpeciality()));
        mechanicTable.setItems(mechanicList);
    }

    private void initServiceTypeTable() {
        colServiceTypeId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getServiceTypeId()));
        colServiceTypeName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
        colServiceTypeDescription.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getDescription()));
        colServiceTypePrice.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getBasePrice()));
        serviceTypeTable.setItems(serviceTypeList);
    }

    private void initPartTable() {
        colPartId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPartId()));
        colPartName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
        colPartUnitPrice.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getUnitPrice()));
        colPartInStockQty.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getInStockQty()));
        partTable.setItems(partList);
    }

    private void initVehicleTable() {
        colVehicleId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getVehicleId()));
        colVehicleCustomer.setCellValueFactory(cell -> {
            if (cell.getValue().getCustomer() != null) {
                return new ReadOnlyStringWrapper(cell.getValue().getCustomer().getFullName());
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });
        colVehicleMake.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getMake()));
        colVehicleModel.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getModel()));
        colVehiclePlate.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getLicensePlate()));
        colVehicleYear.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getYear()));
        vehicleTable.setItems(vehicleList);
    }

    private void loadAllData() throws SQLException {
        loadCustomers();
        loadMechanics();
        loadServiceTypes();
        loadParts();
        loadVehicles();
    }

    private void loadCustomers() {
        try {
            customerList.setAll(customerService.findAll());
        } catch (SQLException e) {
            showError("Failed to load the client list: " + e.getMessage());
        }
    }

    private void loadMechanics() {
        try {
            mechanicList.setAll(mechanicService.findAll());
        } catch (SQLException e) {
            showError("Failed to load the list of mechanics:\n" + e.getMessage());
        }
    }

    private void loadServiceTypes() {
        try {
            serviceTypeList.setAll(serviceTypeService.findAll());
        } catch (SQLException e) {
            showError("Failed to load the Service type list:\n" + e.getMessage());
        }
    }

    private void loadParts() {
        try {
            partList.setAll(partService.findAll());
        } catch (SQLException e) {
            showError("Failed to load parts list:\n" + e.getMessage());
        }
    }

    private void loadVehicles() {
        try {
            vehicleList.setAll(vehicleService.findAll());
        } catch (SQLException e) {
            showError("Failed to load the machine list:\n" + e.getMessage());
        }
    }

    // ==== Actions ==== //

    // ==== Customer === //
    @FXML
    private void onAddCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/customer-user-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create a client user");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(customerTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            CustomerUserDialogController ctrl = loader.getController();
            ctrl.setServices(authService, userFacade);
            ctrl.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (ctrl.isOkClicked()) {
                loadCustomers();
            }
        } catch (Exception e) {
            showError("Failed to open the client creation dialog box:\n" + e.getMessage());
        }
    }


    @FXML
    private void onEditCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(customerTable.getScene().getWindow());
            alert.setTitle("There's no choice");
            alert.setHeaderText("Client not selected");
            alert.setContentText("Please select a client from the table.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/customer-user-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit client user");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(customerTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            CustomerUserDialogController dialogCtrl = loader.getController();
            dialogCtrl.setServices(authService, userFacade);
            dialogCtrl.setDialogStage(dialogStage);
            dialogCtrl.setEditingData(selected.getUser(), selected);

            dialogStage.showAndWait();

            if (dialogCtrl.isOkClicked()) {
                loadCustomers();
            }
        } catch (Exception e) {
            showError("Failed to edit client:\n" + e.getMessage());
        }
    }

    @FXML
    private void onDeleteCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(customerTable.getScene().getWindow());
            alert.setTitle("There's no choice");
            alert.setHeaderText("Client not selected");
            alert.setContentText("Please select a client from the table.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you really want to remove the client \"" +
                        selected.getFullName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.initOwner(customerTable.getScene().getWindow());
        confirm.setTitle("Delete client");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.YES) {
            return;
        }

        try {
            userFacade.deleteCustomerUser(selected.getUser(), selected);
            loadCustomers();
        } catch (SQLException ex) {
            showError("Failed to delete the client:\n" + ex.getMessage());
        }
    }


    // ==== Mechanic === //

    @FXML
    private void onAddMechanic() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/mechanic-user-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create a mechanic user");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mechanicTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            MechanicUserDialogController dialogCtrl = loader.getController();
            dialogCtrl.setServices(authService, userFacade);
            dialogCtrl.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (dialogCtrl.isOkClicked()) {
                loadMechanics();
            }
        } catch (Exception e) {
            showError("Failed to open the mechanic creation dialog:\n" + e.getMessage());
        }
    }
    @FXML
    private void onEditMechanic() {
        Mechanic selected = mechanicTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mechanicTable.getScene().getWindow());
            alert.setTitle("There's no choice");
            alert.setHeaderText("The mechanic is not selected");
            alert.setContentText("Please select a mechanic from the table.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/mechanic-user-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit user-mechanics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mechanicTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            MechanicUserDialogController dialogCtrl = loader.getController();
            dialogCtrl.setServices(authService, userFacade);
            dialogCtrl.setDialogStage(dialogStage);
            dialogCtrl.setEditingData(selected.getUser(), selected);

            dialogStage.showAndWait();

            if (dialogCtrl.isOkClicked()) {
                loadMechanics();
            }
        } catch (Exception e) {
            showError("Failed to edit the mechanic:\n" + e.getMessage());
        }
    }

    @FXML
    private void onDeleteMechanic() {
        Mechanic selected = mechanicTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mechanicTable.getScene().getWindow());
            alert.setTitle("There's no choice");
            alert.setHeaderText("The mechanic is not selected");
            alert.setContentText("Please select a mechanic from the table.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you really want to remove the mechanics \"" +
                        selected.getFullName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.initOwner(mechanicTable.getScene().getWindow());
        confirm.setTitle("Remove the mechanic");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.YES) {
            return;
        }

        try {
            userFacade.deleteMechanicUser(selected.getUser(), selected);

            loadMechanics();
        } catch (SQLException ex) {
            showError("Failed to remove the mechanic:\n" + ex.getMessage());
        }
    }

    // ==== ServiceType === //

    @FXML
    private void onAddServiceType() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/service-type-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Service type");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(serviceTypeTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            ServiceTypeDialogController dialogCtrl = loader.getController();
            dialogCtrl.setServices(authService, serviceTypeService);
            dialogCtrl.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (dialogCtrl.isOkClicked()) {
                loadServiceTypes();
            }
        } catch (Exception e) {
            showError("Failed to open the Service type creation dialog box:\n" + e.getMessage());
        }
    }

    @FXML
    private void onEditServiceType() {
        ServiceType selected = serviceTypeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(serviceTypeTable.getScene().getWindow());
            alert.setTitle("There's no choice");
            alert.setHeaderText("ServiceType not selected");
            alert.setContentText("Please select a row in the table.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/service-type-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Service type");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(serviceTypeTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            ServiceTypeDialogController dialogCtrl = loader.getController();
            dialogCtrl.setServices(authService, serviceTypeService);
            dialogCtrl.setDialogStage(dialogStage);
            dialogCtrl.setServiceType(selected);

            dialogStage.showAndWait();

            if (dialogCtrl.isOkClicked()) {
                loadServiceTypes();
            }
        } catch (Exception e) {
            showError("Failed to edit Service type:\n" + e.getMessage());
        }
    }

    @FXML
    private void onDeleteServiceType() {
        ServiceType selected = serviceTypeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(serviceTypeTable.getScene().getWindow());
            alert.setTitle("There's no choice");
            alert.setHeaderText("Service type not selected");
            alert.setContentText("Please select a row in the table.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you really want to delete Service type \"" +
                        selected.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.initOwner(serviceTypeTable.getScene().getWindow());
        confirm.setTitle("Delete Service type");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.YES) {
            return;
        }

        try {
            serviceTypeService.delete(selected.getServiceTypeId());
            loadServiceTypes();
        } catch (SQLException ex) {
            showError("Failed to delete Service type:\n" + ex.getMessage());
        }
    }

    // ==== Part === //

    @FXML
    private void onAddPart() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/part-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Part");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(partTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            PartDialogController dialogCtrl = loader.getController();
            dialogCtrl.setServices(authService, partService);
            dialogCtrl.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (dialogCtrl.isOkClicked()) {
                loadParts();
            }
        } catch (Exception e) {
            showError("Failed to open the dialog box for creating a part:\n" + e.getMessage());
        }
    }

    @FXML
    private void onEditPart() {
        Part selected = partTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(partTable.getScene().getWindow());
            alert.setTitle("There's no choice");
            alert.setHeaderText("Part not selected");
            alert.setContentText("Please select the line with the part.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pti/datenbank/autowerk/part-dialog.fxml")
            );
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Part");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(partTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            PartDialogController dialogCtrl = loader.getController();
            dialogCtrl.setServices(authService, partService);
            dialogCtrl.setDialogStage(dialogStage);
            dialogCtrl.setPart(selected);

            dialogStage.showAndWait();

            if (dialogCtrl.isOkClicked()) {
                loadParts();
            }
        } catch (Exception e) {
            showError("Unable to edit the part:\n" + e.getMessage());
        }
    }

    @FXML
    private void onDeletePart() {
        Part selected = partTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(partTable.getScene().getWindow());
            alert.setTitle("There's no choice");
            alert.setHeaderText("Part not selected");
            alert.setContentText("Please select the line with the part.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you really want to remove the part \"" +
                        selected.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.initOwner(partTable.getScene().getWindow());
        confirm.setTitle("Delete Part");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.YES) {
            return;
        }

        try {
            partService.delete(selected.getPartId());
            loadParts();
        } catch (SQLException ex) {
            showError("Failed to delete a part:\n" + ex.getMessage());
        }
    }

    // ==== Vehicle === //

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

            VehicleDialogController dialogCtrl = loader.getController();
            dialogCtrl.setServices(authService, vehicleService, customerService);
            dialogCtrl.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (dialogCtrl.isOkClicked()) {
                loadVehicles();
            }
        } catch (Exception e) {
            showError("Failed to open the Create Machine dialog box:\n" + e.getMessage());
        }
    }

    @FXML
    private void onEditVehicle() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(vehicleTable.getScene().getWindow());
            alert.setTitle("There's no choice");
            alert.setHeaderText("The car has not been selected");
            alert.setContentText("Please select a machine from the table.");
            alert.showAndWait();
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

            VehicleDialogController dialogCtrl = loader.getController();
            dialogCtrl.setServices(authService, vehicleService, customerService);
            dialogCtrl.setDialogStage(dialogStage);
            dialogCtrl.setVehicle(selected);

            dialogStage.showAndWait();

            if (dialogCtrl.isOkClicked()) {
                loadVehicles();
            }
        } catch (Exception e) {
            showError("Failed to edit the vehicle:\n" + e.getMessage());
        }
    }

    @FXML
    private void onDeleteVehicle() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(vehicleTable.getScene().getWindow());
            alert.setTitle("There's no choice");
            alert.setHeaderText("The car has not been selectead");
            alert.setContentText("Please select a machine from the table.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you really want to delete the machine (ID=" + selected.getVehicleId() +
                        ") client \"" + selected.getCustomer().getFullName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.initOwner(vehicleTable.getScene().getWindow());
        confirm.setTitle("Delete Vehicle");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.YES) {
            return;
        }

        try {
            vehicleService.delete(selected.getVehicleId());
            loadVehicles();
        } catch (SQLException ex) {
            showError("Failed to delete the machine:\n" + ex.getMessage());
        }
    }

    @FXML private void onLogout() {
        authService.logout();
        HelloApplication.showLogin();
    }
    @FXML private void onExit() { System.exit(0); }

    // ==== Utils ==== //
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }
}