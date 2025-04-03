/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qlct.quanlychitieu;

import com.qlct.pojo.Category;
import com.qlct.pojo.User;
import com.qlct.services.CategoryServices;
import com.qlct.services.UserSessionServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ttthu
 */
public class FXEditCategoryController implements Initializable {

    @FXML
    private ComboBox<String> cbType;
    @FXML
    private TextField txtName;
    @FXML
    private Button btnAdd;
    @FXML
    private TableView<Category> tbCategory;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CategoryServices cs = new CategoryServices();
        cbType.setItems(FXCollections.observableArrayList("Chi", "Thu"));
        loadColumns();
        loadTableData();
    }

    private void loadColumns() {
        TableColumn<Category, String> colName = new TableColumn<>("Tên danh mục");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(200);

        TableColumn<Category, String> colType = new TableColumn<>("Loại danh mục");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setPrefWidth(200);

        TableColumn<Category, Void> colAction = new TableColumn<>();
        colAction.setCellFactory(tc -> new TableCell<>() {
            final Button btnDelete = new Button("Xóa");
            final Button btnEdit = new Button("Sửa");
            final HBox btnGroup = new HBox(5, btnEdit, btnDelete);

            {
                btnDelete.setOnAction(evt -> {
                    Category cate = getTableView().getItems().get(getIndex());
                    int id = cate.getId();
                    CategoryServices cs = new CategoryServices();
                    cs.deleteCategory(id);
                    Utils.getAlert("Xóa thành công!").show();
                    loadTableData();
                });

                btnEdit.setOnAction(evt -> {
                    Category cate = getTableView().getItems().get(getIndex());
                    txtName.setText(String.valueOf(cate.getName()));
                    cbType.getSelectionModel().select(cate.getType());

                    // Gán sự kiện cập nhật
                    btnAdd.setOnAction(event -> updateCategory(cate.getId()));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnGroup);
                }
            }
        });

        tbCategory.getColumns().addAll(colName, colType, colAction);

    }

    private void loadTableData() {
        CategoryServices cs = new CategoryServices();

        try {
            this.tbCategory.setItems(FXCollections.observableList(cs.getCatetogires()));
        } catch (SQLException ex) {
            Logger.getLogger(FXTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearForm() {
        cbType.setValue(null);
        txtName.clear();
    }

    private void updateCategory(int categoryId) {
        String name = txtName.getText();
        String type = cbType.getSelectionModel().getSelectedItem();
//        User currentUser = new User();
        if (name.isEmpty() || type == null) {
            Utils.getAlert("Vui lòng điền đầy đủ tên và loại danh mục").show();
            return;
        }
        if (!name.matches("^[\\p{L}\\s]+$")) {
            Utils.getAlert("Tên danh mục chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt!").show();
            return;
        }
        if (isCategoryNameExists(name, type, categoryId)) {
            Utils.getAlert("Tên danh mục '" + name + "' với loại '" + type + "' đã tồn tại!").show();
            return;
        }
        try {
            CategoryServices cs = new CategoryServices();
            cs.updateCategory(categoryId, name, type,UserSessionServices.getCurrentUser().getId());
            Utils.getAlert("Cập nhật danh mục thành công").show();
            clearForm();
            loadTableData();
            btnAdd.setOnAction(this::addHandler);
        } catch (NumberFormatException ex) {
            Utils.getAlert("Error!").show();
        }
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

    public void addHandler(ActionEvent e) {
        String name = txtName.getText().trim();
        String type = cbType.getSelectionModel().getSelectedItem();
        if (name.isEmpty() || type == null) {
            Utils.getAlert("Vui lòng điền đầy đủ tên và loại danh mục").show();
            return;
        }
        if (!name.matches("^[\\p{L}\\s]+$")) {
            Utils.getAlert("Tên danh mục chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt!").show();
            return;
        }
        if (isCategoryNameExists(name, type, -1)) { // -1 để không loại trừ danh mục nào khi thêm mới
            Utils.getAlert("Tên danh mục '" + name + "' với loại '" + type + "' đã tồn tại!").show();
            return;
        }
        try {

            CategoryServices cs = new CategoryServices();
            Category cate = new Category(0, name, type,UserSessionServices.getCurrentUser().getId());
            cs.addCategory(cate);

            Utils.getAlert("Add successful!").show();
            clearForm();
            loadTableData();
        } catch (NumberFormatException ex) {
            Utils.getAlert("Error").show();
        }
    }

    public void backToTransactions(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXTransactions.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((ImageView) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            Utils.getAlert("Lỗi khi quay lại giao diện giao dịch: " + ex.getMessage()).show();
            Logger.getLogger(FXEditCategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
