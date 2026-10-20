package com.qlct.quanlychitieu;

import com.qlct.services.LoginServices;
import com.qlct.services.UserSessionServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


public class AuthController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;

    @FXML private TextField txtLastName;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtUsernameRegister;
    @FXML private PasswordField txtPasswordRegister;
    @FXML private PasswordField txtCofirmPassword;
    @FXML private TextField txtEmail;
    @FXML private Button btnRegister;

    @FXML
    private void checkLogin() throws SQLException, IOException {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        System.out.println("Attempting login for username: " + username);

        if (LoginServices.checkUserAuth(username, password)) {
            System.out.println("Login successful for: " + username);
            UserSessionServices.login(username);
            App.setRoot("FXTransactions", 1200, 800);
        } else {
            System.out.println("Login failed for: " + username);
            Utils.showAlert("Tên đăng nhập hoặc mật khẩu không chính xác!", null, "Lỗi đăng nhập");
        }
        
        
    }


    @FXML
    private void switchRegister() throws IOException {
        try {
            System.out.println(">>> Switching to register screen...");
            App.setRoot("register", 650, 500);
            System.out.println(">>> Switched to register screen successfully!");
        } catch (Throwable t) {
            System.err.println(">>> FAILED to switch to register screen: " + t.getMessage());
            t.printStackTrace();
        }
    }

    @FXML
    private void switchLogin() throws IOException {
        try {
            System.out.println(">>> Switching to login screen...");
            App.setRoot("login", 650, 500);
            System.out.println(">>> Switched to login screen successfully!");
        } catch (Throwable t) {
            System.err.println(">>> FAILED to switch to login screen: " + t.getMessage());
            t.printStackTrace();
        }
    }

    @FXML
    private void btnRegisterHandler() throws SQLException, IOException {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String username = txtUsernameRegister.getText().trim();
        String password = txtPasswordRegister.getText().trim();
        String confirmPassword = txtCofirmPassword.getText().trim();
        String email = txtEmail.getText().trim();

        String error = LoginServices.validateRegisterInput(firstName, lastName, username, email, password, confirmPassword);
        if (!error.isEmpty()) {
            Utils.showAlert(error, null, "Lỗi đăng ký");
            return;
        }

        if (LoginServices.registerUser(firstName, lastName, username, password, email)) {
            App.setRoot("login", 650, 500);
        } else {
            Utils.showAlert("Đăng ký thất bại. Tài khoản có thể đã tồn tại!", null, "Lỗi");
        }
    }

    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Optional init
    }
}
