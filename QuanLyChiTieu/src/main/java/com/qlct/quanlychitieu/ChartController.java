/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.quanlychitieu;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/**
 *
 * @author QUI
 */
public class ChartController implements Initializable{
        @FXML private ComboBox<String> cbChoseChart;
        
        public void loadComboBoxChart(){
        cbChoseChart.setValue("Thống kê theo biểu đồ");
        List<String> choiceBox = new ArrayList<>();
        choiceBox.add("Thống kê theo biểu đồ");
        choiceBox.add("Thống kê theo bảng");
        cbChoseChart.setItems(FXCollections.observableList(choiceBox));
        cbChoseChart.setValue("Thống kê theo biểu đồ");

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       if (cbChoseChart == null) {
        System.out.println("cbChoseTable is null - check your FXML file!");
    } else {
        loadComboBoxChart();
        cbChoseChart.setOnAction(event -> handleComboBoxChart());
    }

    }
    
    private void handleComboBoxChart() {
        String selectedOption = cbChoseChart.getValue();
        if (selectedOption.equals("Thống kê theo bảng")) {
            switchSceneChart("baocaothongketheobang.fxml");
        } else if (selectedOption.equals("Thống kê theo biểu đồ")) {
            System.out.println(selectedOption);
            switchSceneChart("baocaothongketheobieudo.fxml");
        }
    }
    
     private void switchSceneChart(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = (Stage) cbChoseChart.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
        }
    }
}
