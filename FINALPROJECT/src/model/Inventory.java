package model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<InventoryItem> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public void addItem(InventoryItem item) {
        items.add(item);
    }

    public List<InventoryItem> getItems() {
        return items;
    }
}
