module pti.datenbank.autowerk {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;

    requires org.kordamp.bootstrapfx.core;

    opens pti.datenbank.autowerk to javafx.fxml;
    exports pti.datenbank.autowerk;
}