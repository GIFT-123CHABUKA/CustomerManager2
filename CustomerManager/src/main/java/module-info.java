module org.example.customermanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.customermanager to javafx.fxml;
    exports org.example.customermanager;
}