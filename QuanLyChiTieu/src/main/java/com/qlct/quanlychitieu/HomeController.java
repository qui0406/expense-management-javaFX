package com.qlct.quanlychitieu;

import com.qlct.services.LoginServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    @FXML private CheckBox ckValid;
    @FXML private Button btnRegister;
    
    
    @FXML
    private void checkLogin() throws SQLException{
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        if(LoginServices.checkUserAuth(username, password)){
            showAlertLogin("Login successful", null, "Successful");
        }
        else {
            showAlertLogin("Incorrect", null, "Incorrect");
        }
    }
    
    @FXML
    private void switchRegister() throws IOException{
        App.setRoot("register");
    }
    
    @FXML
    private void btnRegisterHandler() throws SQLException{
        String username = txtUsernameRegister.getText();
        String password = txtPasswordRegister.getText();
        String confirmPassword = txtCofirmPassword.getText();
        String email = txtEmail.getText();
        
        // Kiem tra username da ton tai chua
        
        
        LoginServices.registerUser(username, password, email);
    }
    
    
    private void showAlertLogin(String info, String header, String title){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(info);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
}
