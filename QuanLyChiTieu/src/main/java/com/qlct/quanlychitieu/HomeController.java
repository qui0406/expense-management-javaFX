package com.qlct.quanlychitieu;

import com.qlct.pojo.JdbcUtils;
import com.qlct.services.LoginServices;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class HomeController implements Initializable{

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Button btnSwitchRegister;
    
    @FXML private TextField txtUsernameRegister;
    @FXML private PasswordField txtPasswordRegister;
    @FXML private PasswordField txtCofirmPassword;
    @FXML private TextField txtEmail;
    @FXML private Button btnRegister;
    
    
    @FXML
    private void checkLogin() throws SQLException{
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        
        if(LoginServices.saveUser(username, password)){
            showAlertLogin("Login successful", null, "Successful");
        }
        else {
            showAlertLogin("Incorrect", null, "Incorrect");
        }
    }
    
    @FXML
    private void switchRegister(){
        
    }
    
    @FXML
    private void btnRegisterHandler() throws SQLException{
        String username = txtUsernameRegister.getText().toString();
        String password = txtPasswordRegister.getText().toString();
        String confirmPassword = txtCofirmPassword.getText().toString();
        String email = txtEmail.getText().toString();
        
        LoginServices.registerUser(username, password, email);
    }
    
    
    
    private void showAlertLogin(String info, String header, String title){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(info);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
    
    
    
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
}
