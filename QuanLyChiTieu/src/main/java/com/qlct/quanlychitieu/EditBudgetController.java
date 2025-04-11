/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qlct.quanlychitieu;

import com.qlct.pojo.Budget;
import com.qlct.pojo.Category;
import com.qlct.services.BudgetServices;
import com.qlct.services.CategoryServices;
import com.qlct.services.UserSessionServices;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class EditBudgetController implements Initializable {

    private BudgetServices budgetServices = new BudgetServices();

    @FXML
    TextField txtInputBudget;
    @FXML
    Button btnEditBudget;
    @FXML
    Label lblStatusInEdit;
    @FXML
    TableView<Budget> tbBudget;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        budgetServices.setLblStatusInEdit(lblStatusInEdit);

    }

    private void loadColumns() {
        TableColumn<Budget, String> colAmount = new TableColumn<>("Số tiền");
        tbBudget.setFixedCellSize(40);
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amountBudget"));
        colAmount.setPrefWidth(200);

        TableColumn<Budget, String> colType = new TableColumn<>("Loại ngân sách");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setPrefWidth(200);

        TableColumn<Budget, Void> colAction = new TableColumn<>();
        colAction.setCellFactory(tc -> new TableCell<>() {
            final Button btnEdit = new Button("Sửa");
            final HBox btnGroup = new HBox(5, btnEdit);

            {
                btnEdit.setOnAction(evt -> {
                    Budget budget = getTableView().getItems().get(getIndex());
                    txtInputBudget.setText(String.valueOf(budget.getAmountBudget())); // Giả sử bạn có trường nhập số tiền                    
                    btnEditBudget.setOnAction(e -> {
                        try {
                            budgetServices.updateBudgetTODB(txtInputBudget.getText());
                        } catch (NumberFormatException | SQLException ex) {
                            budgetServices.updateStatus(lblStatusInEdit, "❌ Lỗi kết nối CSDL", "-fx-text-fill: red;");

                            ex.printStackTrace(); //In ra lỗi console để debug
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Budget budget = getTableView().getItems().get(getIndex());
                    setGraphic(btnGroup);
                }
            }
        });

        tbBudget.getColumns().addAll(colAmount, colType, colAction);
    }

    private boolean isCategoryNameExists(String name, String type, int excludeId) {
        CategoryServices cs = new CategoryServices();
        try {
            List<Category> categories = cs.getCatetogires();
            for (Category cate : categories) {
                // Kiểm tra trùng tên và loại, nhưng không so sánh với danh mục đang chỉnh sửa (excludeId)
                if (cate.getName().equalsIgnoreCase(name) && cate.getType().equals(type) && cate.getId() != excludeId) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FXEditCategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void loadTableData() {
        BudgetServices bs = new BudgetServices();
        this.tbBudget.setItems(FXCollections.observableList(bs.getBudgetsForCurrentUser())); // Lấy danh sách ngân sách
    }

}
