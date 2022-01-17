module com.example.javafxproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;


    opens com.example.javafxproject to javafx.fxml;
    exports com.example.javafxproject;
}
