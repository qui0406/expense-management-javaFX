/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qlct.quanlychitieu;

import com.qlct.services.BudgetServices;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class BudgetController implements Initializable {
    
    private BudgetServices budgetServices = new BudgetServices();

    @FXML
    TextField txtInputBudget;
    @FXML
    Button btnAddBudget;
    @FXML
    Label lblStatus;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        budgetServices.setLblStatus(lblStatus);
        
        btnAddBudget.setOnAction (e -> {
            try {
                budgetServices.insertBudgetTODB(txtInputBudget.getText());
            } catch (SQLException ex) {
                budgetServices.updateStatus(lblStatus, "❌ Lỗi kết nối CSDL", "-fx-text-fill: red;");
                ex.printStackTrace(); //In ra lỗi console để debug
            }
        });
    }
    
}
