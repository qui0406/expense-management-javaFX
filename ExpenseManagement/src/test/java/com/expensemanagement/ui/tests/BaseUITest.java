package com.expensemanagement.ui.tests;

import com.expensemanagement.App;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.TimeoutException;

/**
 * BaseUITest - Lớp cơ sở cho toàn bộ các kịch bản kiểm thử giao diện tự động.
 * Quản lý vòng đời JavaFX Stage, cấu hình Headless Mode cho CI/CD pipeline.
 */
public abstract class BaseUITest extends ApplicationTest {

    protected Stage primaryStage;

    @BeforeAll
    public static void setupHeadless() {
        // Tự động nhận diện cấu hình headless khi chạy trên CI (GitHub Actions, Jenkins,...)
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"))
                || Boolean.parseBoolean(System.getProperty("testfx.headless", "false"));

        if (isHeadless) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("glass.platform", "Monocle");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        new App().start(stage);
        stage.toFront();
        stage.requestFocus();
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
    }

    /**
     * Bấm phím Enter để đóng popup cảnh báo (Alert) nếu có xuất hiện
     */
    protected void dismissAlert() {
        sleep(300);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        sleep(200);
    }
}
