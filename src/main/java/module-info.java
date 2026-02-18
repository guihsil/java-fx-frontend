module com.example.freelamarket {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.freelamarket to javafx.fxml;
    exports com.freelamarket;
    exports com.freelamarket.controller;
    opens com.freelamarket.controller to javafx.fxml;

    opens com.freelamarket.model to com.fasterxml.jackson.databind;
}