/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qlct.quanlychitieu;

import com.qlct.services.BudgetServices;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class SelectModeController implements Initializable {
    BudgetServices budgetServices = new BudgetServices();

    @FXML Button btBudget;
    @FXML Button btEditBudget;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        btBudget.setOnAction(event -> {
            try {
                switchToBudget(event);
            } catch (IOException ex) {
                Logger.getLogger(SelectModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        btEditBudget.setOnAction(event -> {
            try {
                switchToEditBudget(event);
            } catch (IOException ex) {
                Logger.getLogger(SelectModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    public void switchToBudget(ActionEvent event) throws IOException {
        if(!budgetServices.isBudgetExistForCurrentMonth()){
            App.setRoot("budget");
        }
    }

    public void switchToEditBudget(ActionEvent event) throws IOException {
        if(budgetServices.isBudgetExistForCurrentMonth()){
            App.setRoot("editbudget");
        }
    }
}
