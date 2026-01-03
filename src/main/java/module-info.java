module com.example.greenaware {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.google.auth.oauth2;
    requires com.google.auth;
    requires firebase.admin;
    requires com.google.api.apicommon;
    requires google.cloud.firestore;
    requires google.cloud.core;
    requires com.google.gson;
    requires com.google.api.client;

    requires org.slf4j;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires annotations;
    requires okhttp3;
    requires org.json;

    opens com.example.greenaware to javafx.fxml;
    exports com.example.greenaware;
    exports controllers;
    opens controllers to javafx.fxml;
}