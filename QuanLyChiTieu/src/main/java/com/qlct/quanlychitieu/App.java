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

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login"), 840, 680);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
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
