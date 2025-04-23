package com.qlct.quanlychitieu;

import com.qlct.pojo.Category;
import com.qlct.services.CategoryServices;
import com.qlct.services.UserSessionServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.Alert;
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
        tbCategory.setFixedCellSize(40);
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

                    // Kiểm tra nếu đây là danh mục mặc định thì không cho xóa
                    if (cate.isDefault()) {
                        Utils.getAlert("Không thể xóa danh mục mặc định của hệ thống!").show();
                        return;
                    }

                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Xác nhận xóa");
                    confirmation.setHeaderText("Bạn có chắc chắn muốn xóa danh mục này?");
                    confirmation.setContentText("Danh mục " + cate.getName() + " (" + cate.getType() + " )");

                    confirmation.showAndWait().ifPresent(response -> {
                        if (response == javafx.scene.control.ButtonType.OK) {
                            CategoryServices cs = new CategoryServices();
                            try {
                                cs.deleteCategory(cate.getId());
                                Utils.getAlert("Xóa thành công").show();
                                loadTableData();
                            } catch (IllegalArgumentException ex) {
                                Utils.getAlert(ex.getMessage()).show();
                            } catch (SQLException ex) {
                                Utils.getAlert("Lỗi khi xóa danh mục: " + ex.getMessage()).show();
                                Logger.getLogger(FXEditCategoryController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                });

                btnEdit.setOnAction(evt -> {
                    Category cate = getTableView().getItems().get(getIndex());
                    txtName.setText(cate.getName());
                    cbType.getSelectionModel().select(cate.getType());
                    btnAdd.setText("Cập nhật");
                    btnAdd.setOnAction(event -> updateCategory(cate.getId()));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Category cate = getTableView().getItems().get(getIndex());
                    if (cate.isDefault()) { // Ẩn nút cho danh mục cố định
                        setGraphic(null);
                    } else {
                        setGraphic(btnGroup);
                    }
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
            Logger.getLogger(FXEditCategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearForm() {
        cbType.setValue(null);
        txtName.clear();
        btnAdd.setText("Thêm");
    }

    private void updateCategory(int categoryId) {
        String name = txtName.getText().trim();
        String type = cbType.getSelectionModel().getSelectedItem();

        // Gọi service để cập nhật danh mục
        try {
            CategoryServices cs = new CategoryServices();
            cs.updateCategory(categoryId, name, type);
            Utils.getAlert("Cập nhật danh mục thành công").show();
            clearForm();
            loadTableData();
            btnAdd.setOnAction(this::addHandler);
        } catch (IllegalArgumentException ex) {
            Utils.getAlert(ex.getMessage()).show();
        } catch (SQLException ex) {
            Utils.getAlert("Lỗi khi cập nhật danh mục: " + ex.getMessage()).show();
            Logger.getLogger(FXEditCategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addHandler(ActionEvent e) {
        String name = txtName.getText().trim();
        String type = cbType.getSelectionModel().getSelectedItem();

        // Kiểm tra dữ liệu đầu vào
        if (name == null || name.isEmpty()) {
            Utils.getAlert("Tên danh mục không được để trống!").show();
            return;
        }
        if (type == null) {
            Utils.getAlert("Vui lòng chọn loại danh mục!").show();
            return;
        }

        // Gọi service để thêm danh mục
        try {
            CategoryServices cs = new CategoryServices();
            Category cate = new Category(0, name, type, false);
            cs.addCategory(cate);
            Utils.getAlert("Thêm danh mục thành công!").show();
            clearForm();
            loadTableData();
        } catch (IllegalArgumentException ex) {
            Utils.getAlert(ex.getMessage()).show();
        } catch (SQLException ex) {
            Utils.getAlert("Lỗi khi thêm danh mục: " + ex.getMessage()).show();
            Logger.getLogger(FXEditCategoryController.class.getName()).log(Level.SEVERE, null, ex);
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
