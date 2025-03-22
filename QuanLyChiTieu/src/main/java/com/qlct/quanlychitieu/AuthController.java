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

public class AuthController implements Initializable{
    
    
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Button btnSwitchRegister;
    
    @FXML private TextField txtLastName;
    @FXML private TextField txtFirstName;
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
    private void btnRegisterHandler() throws SQLException, IOException{
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String username = txtUsernameRegister.getText();
        String password = txtPasswordRegister.getText();
        String confirmPassword = txtCofirmPassword.getText();
        String email = txtEmail.getText();
        
        String mess= isValidPassword(firstName, lastName, username, password, confirmPassword);
        if (mess.equals("")){
            if(LoginServices.registerUser(firstName, lastName, username, password, email)){
                showAlertLogin("Lưu thành công", null, "Lưu thành công");
                App.setRoot("login");
            }else {
                showAlertLogin("Lưu thất bại", null, "Lưu thất bại");
            }
        }
        else{
            showAlertLogin(mess, null, mess);
            
        }
    }
    
    public boolean checkIsEmptyFirstName(String firstName){
        return firstName.isEmpty();
    }
    
    public boolean checkIsEmptyLastName(String lastName){
        return lastName.isEmpty();
    }
    
    public boolean checkUsernameValid(String username){
        return username.isEmpty();
    }
    
    public boolean checkUsernameLengthValid(String username){
        return username.length() < 3;
    }
    
    public boolean checkPasswordIsEmpty(String password){
        return password == null || password.isEmpty();
    }
    
    public boolean checkConfirmPasswordIsEmpty(String confirmPassword){
        return confirmPassword == null || confirmPassword.isEmpty();
    }
    
    public boolean checkPasswordLengthValid(String password){
        return password.length() < 8;
    } 
    
    public boolean checkPasswordSpecialCharacter(String password){
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return !password.matches(passwordPattern);
    } 
    
    public boolean checkPasswordEqualConfirm(String password, String confirmPassword){
        return !password.equals(confirmPassword);
    }
    
    public String isValidPassword(String firstName, String lastName, String username, String password, String confirmPassword) {
        // Kiểm tra null hoặc rỗng
        String mess="";
        
        if (checkIsEmptyLastName(lastName)) {
            mess = "Họ không được để trống!";
            return mess;
        }
        if (checkIsEmptyFirstName(firstName)) {
            mess = "Tên không được để trống!";
            return mess;
        }
        if (checkUsernameValid(username)) {
            mess = "Username không được để trống!";
            return mess;
        }
        if(checkUsernameLengthValid(username)){
            mess = "Username phải từ 3 kí tự trở lên";
            return mess;
        }
        
        if (checkPasswordIsEmpty(password)) {
            mess = "Mật khẩu không được để trống!";
            return mess;
        }
        
        if (checkConfirmPasswordIsEmpty(confirmPassword)) {
            mess = "Xác nhận mật khẩu không được để trống!";
            return mess;
        }

        // Kiểm tra độ dài
        if (checkPasswordLengthValid(password)) {
            mess= "Mật khẩu phải có ít nhất 8 ký tự!";
            return mess;
        }

        // Kiểm tra có ít nhất 1 chữ hoa, 1 chữ thường, 1 số, 1 ký tự đặc biệt
        
        if (checkPasswordSpecialCharacter(password)) {
            mess = "Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt!";
            return mess;
        }

        // Kiểm tra password và confirmPassword có giống nhau không
        if (checkPasswordEqualConfirm(password, confirmPassword)) {
            mess= "Mật khẩu xác nhận không khớp!";
            return mess;
        }
        // Nếu tất cả điều kiện đều đúng
        return mess;
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
