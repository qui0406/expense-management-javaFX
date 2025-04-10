/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.quanlychitieu;

import java.io.IOException;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author ttthu
 */
public class Utils {
    public static Alert getAlert(String content) {
        return new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
    }
    
    public static void showAlertLogin(String info, String header, String title){
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
    
}