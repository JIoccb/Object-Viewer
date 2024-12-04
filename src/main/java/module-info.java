module com.cgvsu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    // junit;


    opens com.cgvsu to javafx.fxml;
    exports com.cgvsu;
}