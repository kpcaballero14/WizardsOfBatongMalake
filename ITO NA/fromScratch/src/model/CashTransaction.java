package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CashTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private String description;
    private double amount;
    private LocalDateTime dateTime;

    public CashTransaction(String type, String description, double amount) {
        this(type, description, amount, LocalDateTime.now());
    }

    public CashTransaction(String type, String description, double amount, LocalDateTime dateTime) {
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}