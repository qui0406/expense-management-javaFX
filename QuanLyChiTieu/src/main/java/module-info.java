module com.qlct.quanlychitieu {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.qlct.quanlychitieu to javafx.fxml;
    exports com.qlct.quanlychitieu;
    exports com.qlct.pojo;
    exports com.qlct.services;
}
