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
import javafx.stage.Stage;
import pti.datenbank.autowerk.HelloApplication;
import pti.datenbank.autowerk.models.Part;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.PartService;

import java.math.BigDecimal;

public class MechanicController {

    @FXML private MenuItem logoutMenu;
    @FXML private MenuItem exitMenu;
    @FXML private Tab tabProfile;
    @FXML private VBox profileRoot;
    @FXML private TableView<Part> partTable;
    @FXML private TableColumn<Part, Integer> colPartId;
    @FXML private TableColumn<Part, String> colPartName;
    @FXML private TableColumn<Part, String> colPartManufacturer;
    @FXML private TableColumn<Part, BigDecimal> colPartUnitPrice;
    @FXML private TableColumn<Part, Integer> colPartInStockQty;

    private final ObservableList<Part> partList = FXCollections.observableArrayList();
    private PartService partService;


    private AuthService authService;
    private MechanicProfileController profileController;

    public void setAuthService(AuthService authService) {
        this.authService = authService;
        this.partService = new PartService(authService);
        loadParts();

        if (profileController != null) {
            profileController.setServices(authService);
            profileController.loadProfile(); // Загружаем только после установки сервисов
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
    }

    private void initPartTable() {
        colPartId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPartId()));
        colPartName.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
        colPartManufacturer.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getManufacturer()));
        colPartUnitPrice.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getUnitPrice()));
        colPartInStockQty.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getInStockQty()));
        partTable.setItems(partList);
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