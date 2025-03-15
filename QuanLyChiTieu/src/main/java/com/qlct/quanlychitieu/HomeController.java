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
        
        isValidPassword(password, confirmPassword);
        LoginServices.registerUser(username, password, email);
        showAlertLogin("Lưu thành công", null, "Lưu thành công");
    }
    
    public static boolean isValidPassword(String password, String confirmPassword) {
        // Kiểm tra null hoặc rỗng
        if (password == null || confirmPassword == null || password.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("Mật khẩu không được để trống!");
            return false;
        }

        // Kiểm tra độ dài
        if (password.length() < 8) {
            System.out.println("Mật khẩu phải có ít nhất 8 ký tự!");
            return false;
        }

        // Kiểm tra có ít nhất 1 chữ hoa, 1 chữ thường, 1 số, 1 ký tự đặc biệt
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!password.matches(passwordPattern)) {
            System.out.println("Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt!");
            return false;
        }

        // Kiểm tra password và confirmPassword có giống nhau không
        if (!password.equals(confirmPassword)) {
            System.out.println("Mật khẩu xác nhận không khớp!");
            return false;
        }

        // Nếu tất cả điều kiện đều đúng
        return true;
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
