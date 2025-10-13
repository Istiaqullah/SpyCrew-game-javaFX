module Log{
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires javafx.media;
    requires java.desktop;


    opens Log to javafx.fxml;
    exports Log;
    exports main;
    opens main to javafx.fxml;
}