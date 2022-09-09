module dev.damocles.colordashboard {
    requires javafx.controls;
    requires javafx.fxml;


    opens dev.damocles.colordashboard to javafx.fxml;
    exports dev.damocles.colordashboard;
}