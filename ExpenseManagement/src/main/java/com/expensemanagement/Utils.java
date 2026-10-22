/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.expensemanagement;
import com.expensemanagement.services.UserSessionServices;
import java.io.IOException;
import java.text.NumberFormat;
import javafx.util.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author ttthu
 */
public class Utils {
    public static Alert getAlert(String content) {
        return new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
    }
    
    public static void showAlert(String info, String header, String title){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(info);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
    
    
     public static void showAutoCloseAlert(String title, String header, String content, int seconds) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.1), e -> {
            try {
                App.setRoot("login", 650, 500);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }));
        timeline.play();
    }
     
    public static void loadUsername(Label lbUsername){
        lbUsername.setText(UserSessionServices.getCurrentUser().getUsername());
    }
    
    
    public void switchSceneTable(String fxmlFile, Button btn) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) btn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { // In ra chi tiết lỗi
            // In ra chi tiết lỗi
            System.out.println("Failed to load FXML: " + e);
        }
    }
    
    public static String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
    
    public static void logout(Button logoutButton, String fxmlFile) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận đăng xuất");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn đăng xuất không?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                UserSessionServices.logout();// Đăng xuất
                System.out.println("User logged out.");  // Log kiểm tra
                App.setRoot("login", 650, 500);
            } catch (IOException e) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, "Lỗi khi đăng xuất: ", e);
                Utils.showAlert("Lỗi hệ thống!", null, "Không thể đăng xuất. Vui lòng thử lại.");
            }
        }
    }
}