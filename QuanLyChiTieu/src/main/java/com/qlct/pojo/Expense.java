/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.pojo;

import java.time.LocalDate;


/**
 *
 * @author QUI
 */
public class Expense {
    private int userId;
    private String type;
    private int categoryId;
    private String categoryName;
    private double amount;
    
    private LocalDate fromDate;
    private LocalDate toDate;
    
    public Expense(String type, int userId){
         this.userId = userId;
        this.type= type;
    }
    
    public Expense(int userId, String type, int categoryId, String categoryName, double amount) {
        this.userId = userId;
        this.type= type;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
    }
    
    public Expense(int categoryId, String categoryName, double amount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
    }
    
    public Expense(LocalDate fromDate, LocalDate toDate, int categoryId, String categoryName, double amount) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the categoryId
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * @return the fromDate
     */
    public LocalDate getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the toDate
     */
    public LocalDate getToDate() {
        return toDate;
    }

    /**
     * @param toDate the toDate to set
     */
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    /**
     * @return the id
     */
    
   

}
