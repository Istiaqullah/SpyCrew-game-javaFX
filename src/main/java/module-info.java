module Log  {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires javafx.media;


    opens Log to javafx.fxml;
    exports Log;
    opens Profile to javafx.fxml;
    exports Profile;
    exports main;
    opens main to javafx.fxml;
}