package com.qlct.ui.pages;

import org.testfx.api.FxRobot;

/**
 * TransactionsPage - Page Object đại diện cho màn hình Quản lý Giao Dịch (FXTransactions.fxml).
 */
public class TransactionsPage extends BasePage {

    // Locators
    public static final String TXT_USERNAME = "#txtUsername";
    public static final String TXT_AMOUNT = "#txtAmount";
    public static final String TXT_NOTE = "#txtNote";
    public static final String DP_DATE = "#btDate";
    public static final String CB_TYPE = "#cbType";
    public static final String CB_CATES = "#cbCates";
    public static final String BTN_SAVE = "#btnSave";
    public static final String BTN_CANCEL = "#btnCancel";
    public static final String TABLE_TRANSACTIONS = "#tbTransaction";
    public static final String BTN_LOGOUT = "#logoutBtnTransaction";
    public static final String TXT_TOTAL_AMOUNT = "#txtTotalAmount";
    public static final String TXT_EXPENSE_AMOUNT = "#txtExpenseAmount";

    public TransactionsPage(FxRobot robot) {
        super(robot);
    }

    public boolean isPageLoaded() {
        return isDisplayed(TXT_AMOUNT) && isDisplayed(BTN_SAVE) && isDisplayed(TABLE_TRANSACTIONS);
    }

    public TransactionsPage enterAmount(String amount) {
        type(TXT_AMOUNT, amount);
        return this;
    }

    public TransactionsPage enterNote(String note) {
        type(TXT_NOTE, note);
        return this;
    }

    public void clickSave() {
        click(BTN_SAVE);
    }

    public void clickCancel() {
        click(BTN_CANCEL);
    }

    public void clickLogout() {
        click(BTN_LOGOUT);
    }

    public String getCurrentUserText() {
        return getText(TXT_USERNAME);
    }

    public String getTotalIncomeText() {
        return getText(TXT_TOTAL_AMOUNT);
    }

    public String getTotalExpenseText() {
        return getText(TXT_EXPENSE_AMOUNT);
    }
}
