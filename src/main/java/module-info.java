module com.hilos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.hilos to javafx.fxml;
    exports com.hilos;
}
