module pti.datenbank.autowerk {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;
    requires org.kordamp.bootstrapfx.core;

    // Your controllers only need to open to the FXML loader:
    opens pti.datenbank.autowerk.controllers to javafx.fxml;

    // Your models need to open for reflection by PropertyValueFactory:
    opens pti.datenbank.autowerk.models to javafx.base;

    // If you load other classes via FXML, open them to javafx.fxml:
    opens pti.datenbank.autowerk.services to javafx.fxml;
    opens pti.datenbank.autowerk.dao to javafx.fxml;
    opens pti.datenbank.autowerk.enums to javafx.fxml;

    // Generally you only export your root package if you have public APIs:
    exports pti.datenbank.autowerk;
}
