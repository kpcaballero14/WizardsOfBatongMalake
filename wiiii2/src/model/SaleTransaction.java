package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SaleTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemName;
    private int quantity;
    private double unitPrice;
    private double totalAmount;
    private double amountPaid;
    private double change;
    private LocalDateTime dateTime;

    public SaleTransaction(String itemName, int quantity, double unitPrice, double amountPaid) {
        this(itemName, quantity, unitPrice, amountPaid, LocalDateTime.now());
    }

    public SaleTransaction(String itemName, int quantity, double unitPrice, double amountPaid, LocalDateTime dateTime) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = unitPrice * quantity;
        this.amountPaid = amountPaid;
        this.change = amountPaid - totalAmount;
        this.dateTime = dateTime;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public double getChange() {
        return change;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}