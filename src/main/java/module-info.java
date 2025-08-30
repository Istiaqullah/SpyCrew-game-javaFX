module logFile {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;


    opens logFile to javafx.fxml;
    exports logFile;
}