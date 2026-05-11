package model;

import java.io.Serializable;

public class InventoryItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemName;
    private String description;
    private int stocks;
    private double price;
    private int lowStockThreshold;
    private int usedCount;
    private String imagePath;

    public InventoryItem(String itemName, String description, int stocks, double price, int lowStockThreshold, String imagePath) {
        this.itemName = itemName;
        this.description = description;
        this.stocks = stocks;
        this.price = price;
        this.lowStockThreshold = lowStockThreshold;
        this.imagePath = imagePath;
        this.usedCount = 0;
    }

    public InventoryItem(String itemName, String description, int stocks, double price, int lowStockThreshold) {
        this(itemName, description, stocks, price, lowStockThreshold, "");
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

    public int getUsedCount() {
        return usedCount;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isLowStock() {
        return stocks <= lowStockThreshold;
    }

    public void addStock(int amount) {
        if (amount > 0) {
            stocks += amount;
        }
    }

    public boolean consumeStock(int amount) {
        if (amount > 0 && stocks >= amount) {
            stocks -= amount;
            usedCount += amount;
            return true;
        }
        return false;
    }

    public boolean reduceStock(int amount) {
        if (amount > 0 && stocks >= amount) {
            stocks -= amount;
            return true;
        }
        return false;
    }

    public void setStocks(int stocks) {
        this.stocks = Math.max(0, stocks);
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = Math.max(0, usedCount);
    }

    @Override
    public String toString() {
        return itemName;
    }
}