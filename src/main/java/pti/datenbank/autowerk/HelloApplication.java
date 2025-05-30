package pti.datenbank.autowerk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pti.datenbank.autowerk.controllers.AuthController;
import pti.datenbank.autowerk.services.AuthService;

import java.lang.reflect.Method;

public class HelloApplication extends Application {
    private static Stage primaryStage;
    private static final AuthService authService = new AuthService();

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Autowerk - Login");
        showLogin();
        primaryStage.show();
    }

    public static void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("auth.fxml"));
            Parent root = loader.load();
            AuthController controller = loader.getController();
            controller.setAuthService(authService);
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showByRole() {
        String role = authService.getCurrentUser()
                .getRole()
                .getRoleName();
        switch (role) {
            case "Admin":
                showAdminView();
                break;
            case "Customer":
                showCustomerView();
                break;
            case "Mechanic":
                showMechanicView();
                break;
            default:
                // Проще вернуть на логин или показать ошибку
                showLogin();
                break;
        }
    }

    public static void showAdminView() {
        loadAndShow("admin-dashboard.fxml",
                "Autowerk - Admin Dashboard");
    }

    public static void showCustomerView() {
        loadAndShow("customer-dashboard.fxml",
                "Autowerk - Customer Dashboard");
    }

    public static void showMechanicView() {
        loadAndShow("mechanic-dashboard.fxml",
                "Autowerk - Mechanic Dashboard");
    }


    private static void loadAndShow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(fxmlPath)
            );
            Parent root = loader.load();

            // Если у контроллера есть сеттер для AuthService — инжектим
            Object controller = loader.getController();
            try {
                Method m = controller.getClass()
                        .getMethod("setAuthService", AuthService.class);
                m.invoke(controller, authService);
            } catch (NoSuchMethodException ignored) {
                // контроллер может и не требовать AuthService
            }

            primaryStage.setTitle(title);
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}