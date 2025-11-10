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

    opens MiniGame1 to javafx.fxml;
    exports  MiniGame1;

opens MiniGame2 to javafx.fxml;
    exports MiniGame2;

    opens MiniGame3 to javafx.fxml;
    exports MiniGame3;

    opens MiniGame4 to javafx.fxml;
    exports MiniGame4;

}