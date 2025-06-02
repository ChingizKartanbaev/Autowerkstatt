package pti.datenbank.autowerk.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pti.datenbank.autowerk.models.Customer;
import pti.datenbank.autowerk.models.User;
import pti.datenbank.autowerk.models.Vehicle;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.CustomerService;
import pti.datenbank.autowerk.services.VehicleService;

import java.sql.SQLException;
import java.util.List;

public class VehicleDialogController {

    @FXML private ComboBox<Customer> cbCustomer;
    @FXML private TextField tfMake;
    @FXML private TextField tfModel;
    @FXML private TextField tfPlate;
    @FXML private TextField tfYear;

    private Stage dialogStage;
    private boolean okClicked = false;
    private AuthService authService;
    private VehicleService vehicleService;
    private CustomerService customerService;

    private Vehicle vehicle;

    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    public void setServices(AuthService authService,
                            VehicleService vehicleService,
                            CustomerService customerService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        loadCustomersIntoComboBox();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        tfMake.setText(vehicle.getMake());
        tfModel.setText(vehicle.getModel());
        tfPlate.setText(vehicle.getLicensePlate());
        tfYear.setText(String.valueOf(vehicle.getYear()));

        cbCustomer.getSelectionModel().select(vehicle.getCustomer());

        User current = authService.getCurrentUser();
        if (current.getRole().getRoleName().equals("Customer")) {
            cbCustomer.setDisable(true);
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    private void loadCustomersIntoComboBox() {
        User current = authService.getCurrentUser();
        String roleName = current.getRole().getRoleName();

        try {
            if ("Admin".equals(roleName)) {
                List<Customer> all = customerService.findAll();
                customerList.setAll(all);
            }
            else if ("Customer".equals(roleName)) {
                Customer self = customerService.findByUserId(current.getUserId());
                customerList.clear();
                if (self != null) {
                    customerList.add(self);
                }
                cbCustomer.setDisable(true);
            }
            else {
                customerList.clear();
                cbCustomer.setDisable(true);
            }

            cbCustomer.setItems(customerList);

            cbCustomer.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getFullName());
                    }
                }
            });

            cbCustomer.setButtonCell(new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getFullName());
                    }
                }
            });

            if ("Customer".equals(roleName) && !customerList.isEmpty()) {
                cbCustomer.getSelectionModel().selectFirst();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Error when loading the list of clients:\n" + ex.getMessage()
            ).showAndWait();
        }
    }

    @FXML
    private void handleOk() {
        StringBuilder err = new StringBuilder();

        Customer selectedCustomer = cbCustomer.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            err.append("– select a client from the list\n");
        }

        if (tfMake.getText().trim().isEmpty()) {
            err.append("– enter the make of the machine\n");
        }
        if (tfModel.getText().trim().isEmpty()) {
            err.append("– enter the machine model\n");
        }
        if (tfPlate.getText().trim().isEmpty()) {
            err.append("– enter the plate number\n");
        }
        if (tfYear.getText().trim().isEmpty()) {
            err.append("– enter the year of manufacture\n");
        } else {
            try {
                Integer.parseInt(tfYear.getText().trim());
            } catch (NumberFormatException ex) {
                err.append("– the “Year” field must be an integer number\n");
            }
        }

        if (err.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(dialogStage);
            alert.setTitle("Incorrect data");
            alert.setHeaderText("Please correct:");
            alert.setContentText(err.toString());
            alert.showAndWait();
            return;
        }

        try {
            if (vehicle == null) {
                vehicle = new Vehicle();
            }
            vehicle.setCustomer(selectedCustomer);
            vehicle.setMake(tfMake.getText().trim());
            vehicle.setModel(tfModel.getText().trim());
            vehicle.setLicensePlate(tfPlate.getText().trim());
            vehicle.setYear(Integer.parseInt(tfYear.getText().trim()));

            if (vehicle.getVehicleId() == 0) {
                vehicleService.create(vehicle);
            } else {
                vehicleService.update(vehicle);
            }

            okClicked = true;
            dialogStage.close();
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Error when saving");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}