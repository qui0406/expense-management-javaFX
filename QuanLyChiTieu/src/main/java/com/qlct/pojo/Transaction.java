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
    private int userId;

    public Transaction() {
    }

    public Transaction(double amount, int categoryId, LocalDate date, String note, int userId) {
        this.id = 0;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    public Transaction(int id, double amount, int categoryId, LocalDate date, String note, int userId) {
        this.id = id;
        this.amount = amount;
        this.categoryId = categoryId;
        this.date = date;
        this.note = note;
        this.userId = userId;

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

}
