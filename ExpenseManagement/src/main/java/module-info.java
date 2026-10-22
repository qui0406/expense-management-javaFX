module com.expensemanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens com.expensemanagement to javafx.fxml;
    exports com.expensemanagement;
    exports com.expensemanagement.pojo;
    exports com.expensemanagement.services;
    opens com.expensemanagement.services;
    requires jbcrypt;
    requires java.base;
    requires javafx.swingEmpty;
    requires org.apache.commons.validator;
}
