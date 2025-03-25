/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.pojo;

import java.time.LocalDate;

/**
 *
 * @author ttthu
 */
public class Transaction {
    private int id;
    private double amount;
    private int categoryId;
    private LocalDate date;
    private String note;
    public Transaction(){
    }
    public Transaction(double amount,  int categoryId, LocalDate date,String note) {
        this.id = 0; // id sẽ được cơ sở dữ liệu tự động gán
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.categoryId = categoryId;
    }
    public Transaction(int id, double amount, int categoryId, LocalDate date, String note){
        this.id = id;
        this.amount = amount;
        this.categoryId = categoryId;
        this.date = date;
        this.note = note;
    
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
     * @return the category
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return the idTran
     */
    public int getIdTran() {
        return id;
    }

    /**
     * @param idTran the idTran to set
     */
    public void setIdTran(int idTran) {
        this.id = idTran;
    }


}

