package model;

import java.io.Serializable;

public class InventoryUsage implements Serializable {
    private static final long serialVersionUID = 1L;

    private InventoryItem inventoryItem;
    private int quantityNeeded;

    public InventoryUsage(InventoryItem inventoryItem, int quantityNeeded) {
        this.inventoryItem = inventoryItem;
        this.quantityNeeded = quantityNeeded;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public int getQuantityNeeded() {
        return quantityNeeded;
    }

    @Override
    public String toString() {
        return inventoryItem.getItemName() + " x" + quantityNeeded;
    }
}