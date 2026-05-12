package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemName;
    private String description;
    private double price;
    private double profitPercentage;
    private List<InventoryUsage> ingredients;
    private String imagePath;
    
    public MenuItem(String itemName, String description, double price, double profitPercentage, String imagePath) {
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.profitPercentage = profitPercentage;
        this.imagePath = imagePath;
        this.ingredients = new ArrayList<>();
    }

    public String getImagePath() {
        return imagePath;
    }
    
    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public double getProfitPercentage() {
        return profitPercentage;
    }

    public List<InventoryUsage> getIngredients() {
        return ingredients;
    }

    public void addIngredient(InventoryItem inventoryItem, int quantityNeeded) {
        ingredients.add(new InventoryUsage(inventoryItem, quantityNeeded));
        updatePriceBasedOnIngredients();
    }

    public double getIngredientCost() {
        double total = 0;

        for (InventoryUsage usage : ingredients) {
            total += usage.getInventoryItem().getPrice() * usage.getQuantityNeeded();
        }

        return total;
    }

    public void setProfitPercentage(double profitPercentage) {
        this.profitPercentage = profitPercentage;
        updatePriceBasedOnIngredients();
    }

    public void updatePriceBasedOnIngredients() {
        double ingredientCost = getIngredientCost();
        this.price = ingredientCost + (ingredientCost * profitPercentage / 100);
    }

    public String getIngredientsSummary() {
        if (ingredients.isEmpty()) {
            return "No ingredients listed";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < ingredients.size(); i++) {
            builder.append(ingredients.get(i));

            if (i < ingredients.size() - 1) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
}