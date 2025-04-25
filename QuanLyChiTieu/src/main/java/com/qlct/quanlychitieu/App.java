package com.qlct.quanlychitieu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.layout.Pane;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;
        scene = new Scene(loadFXML("login"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    static void setRoot(String fxml, double width, double height) throws IOException {
        Parent root = loadFXML(fxml);
        Scene newScene = new Scene(root, width, height);

        stage.setScene(newScene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/qlct/quanlychitieu/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setContentPane(Pane contentPane, String fxml) throws IOException {
        Parent newContent = loadFXML(fxml); // Nạp nội dung từ file FXML
        contentPane.getChildren().clear(); // Xóa nội dung cũ
        contentPane.getChildren().add(newContent); // Thêm nội dung mới vào contentPane
    }

    public static void main(String[] args) {
        launch();
    }
}
