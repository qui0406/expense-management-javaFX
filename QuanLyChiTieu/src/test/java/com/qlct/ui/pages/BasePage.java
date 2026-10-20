package com.qlct.ui.pages;

import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * BasePage - Lớp cơ sở cho toàn bộ Page Objects theo mô hình Page Object Model (POM).
 * Đóng gói toàn bộ các tương tác FxRobot cấp thấp, cung cấp API trực quan cho kiểm thử tự động UI.
 */
public abstract class BasePage {

    protected final FxRobot robot;

    public BasePage(FxRobot robot) {
        this.robot = robot;
    }

    /**
     * Tìm Node theo CSS selector hoặc fx:id (ví dụ: "#txtUsername")
     */
    protected <T extends Node> T find(String selector) {
        return robot.lookup(selector).query();
    }

    /**
     * Kiểm tra một thành phần giao diện có đang hiển thị trên Scene hay không
     */
    public boolean isDisplayed(String selector) {
        try {
            return robot.lookup(selector).tryQuery().isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click vào một phần tử giao diện (hỗ trợ fire() an toàn trên ButtonBase để tránh lỗi tọa độ trên macOS Retina / Headless)
     */
    protected void click(String selector) {
        Node node = find(selector);
        if (node instanceof javafx.scene.control.ButtonBase) {
            robot.interact(() -> ((javafx.scene.control.ButtonBase) node).fire());
        } else {
            robot.clickOn(selector);
        }
    }

    /**
     * Nhập text vào trường nhập liệu (click để kích hoạt focus, sau đó nhập nội dung an toàn)
     */
    protected void type(String selector, String text) {
        click(selector);
        Node node = find(selector);
        if (node instanceof TextInputControl) {
            robot.interact(() -> ((TextInputControl) node).setText(text != null ? text : ""));
        } else {
            robot.eraseText(50);
            if (text != null && !text.isEmpty()) {
                robot.write(text);
            }
        }
    }

    /**
     * Lấy text hiển thị của một Node bất kỳ (Label, Button, TextField, Text)
     */
    public String getText(String selector) {
        try {
            Node node = find(selector);
            if (node instanceof Labeled) {
                return ((Labeled) node).getText();
            } else if (node instanceof TextInputControl) {
                return ((TextInputControl) node).getText();
            } else if (node instanceof Text) {
                return ((Text) node).getText();
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * Kiểm tra trạng thái kích hoạt (Disable / Enable) của một phần tử
     */
    public boolean isEnabled(String selector) {
        Node node = find(selector);
        return node != null && !node.isDisable();
    }

    /**
     * Chờ một phần tử xuất hiện trong khoảng thời gian nhất định (Explicit Wait)
     */
    public void waitFor(String selector, long timeoutSeconds) {
        try {
            WaitForAsyncUtils.waitFor(timeoutSeconds, TimeUnit.SECONDS, () -> isDisplayed(selector));
        } catch (TimeoutException e) {
            throw new AssertionError("Hết thời gian chờ phần tử " + selector + " hiển thị", e);
        }
    }

    /**
     * Tạm dừng trong một khoảng thời gian (dùng khi cần quan sát animation hoặc chuyển cảnh)
     */
    public void pause(long millis) {
        robot.sleep(millis);
    }
}
