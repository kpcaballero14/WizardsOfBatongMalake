package model;

import java.io.Serializable;

public class InventoryItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemName;
    private String description;
    private int stocks;
    private double price;
    private int lowStockThreshold;

    public InventoryItem(String itemName, String description, int stocks, double price, int lowStockThreshold) {
        this.itemName = itemName;
        this.description = description;
        this.stocks = stocks;
        this.price = price;
        this.lowStockThreshold = lowStockThreshold;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public int getStocks() {
        return stocks;
    }

    public double getPrice() {
        return price;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public boolean isLowStock() {
        return stocks <= lowStockThreshold;
    }

    public void addStock(int amount) {
        if (amount > 0) {
            stocks += amount;
        }
    }

    public boolean reduceStock(int amount) {
        if (amount > 0 && stocks >= amount) {
            stocks -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return itemName;
    }
}