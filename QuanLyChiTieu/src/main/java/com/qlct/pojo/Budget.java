/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.pojo;

import java.time.LocalDate;

/**
 *
 * @author DELL
 */
public class Budget {
    private int id;                
    private double amountBudget;    
    private LocalDate createdDate;       
    private LocalDate updatedDate;       
    private int userId;            
    private String type;      
    private int categoryId;
    
    public Budget() {}
    
//    public Budget(double amountBudget, LocalDate createdDate, LocalDate updatedDate, int userId, String type) {
//        this.id = 0;
//        this.amountBudget = amountBudget;
//        this.createdDate = createdDate;
//        this.updatedDate = updatedDate;
//        this.userId = userId;
//        this.type = type;
//    }
    
    public Budget(int id, double amountBudget, LocalDate createdDate, LocalDate updatedDate, int userId, String type, int categoryId) {
        this.id = id;
        this.amountBudget = amountBudget;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.userId = userId;
        this.type = type;
        this.categoryId = categoryId;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the amountBudget
     */
    public double getAmountBudget() {
        return amountBudget;
    }

    /**
     * @param amountBudget the amountBudget to set
     */
    public void setAmountBudget(double amountBudget) {
        this.amountBudget = amountBudget;
    }

    /**
     * @return the createdDate
     */
    public LocalDate getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the updatedDate
     */
    public LocalDate getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(LocalDate updatedDate) {
        this.updatedDate = updatedDate;
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

    @Override
    public String toString() {
        return "Budget{" +
               "categoryId=" + categoryId +
               ", amountBudget=" + amountBudget +
               ", type='" + type + '\'' +
               '}';
    }
   
}
