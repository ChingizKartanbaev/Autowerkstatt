package pti.datenbank.autowerk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pti.datenbank.autowerk.dao.DBConnection;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

    }

    public static void main(String[] args) throws SQLException {

        DBConnection.getConnection();
        launch();
        DBConnection.close();
    }
}