package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.Cashier;
import model.Inventory;
import model.InventoryItem;
import model.Manager;
import model.Menu;
import model.MenuItem;
import model.User;

public class RetailSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String businessName;
    private List<User> users;
    private Inventory inventory;
    private Menu menu;

    public RetailSystem() {
        this.businessName = "Wizards of Batong Malake";
        this.users = new ArrayList<>();
        this.inventory = new Inventory();
        this.menu = new Menu();
    }

    public void initializeDefaultData() {
        if (!users.isEmpty()) {
            return;
        }

        users.add(new Manager("manager", "1234", "Project Manager"));
        users.add(new Cashier("cashier", "1234", "Project Cashier"));

        InventoryItem lizardTail = new InventoryItem("Lizard Tail", "Still twitching like it forgot it’s no longer attached. Great for healing… slightly unsettling to watch.", 10, 69, 3);
        InventoryItem cyclopsEye = new InventoryItem("Cyclop's Eye", "Always staring, never blinking. It sees everything, yes, even that thing you’re trying to hide. Awkward.", 10, 1000, 3);
        InventoryItem dragonTail = new InventoryItem("Dragon's Tail", "A burnt piece that refuses to cool down. If it starts glowing brighter, run.", 10, 3000, 3);
        InventoryItem tearsOfAChild = new InventoryItem("Tears of a Child", "Pure, innocent, and a little sad. Collecting them is the hard part… explaining it is harder.", 10, 1500, 3);
        InventoryItem sirensHeart = new InventoryItem("Siren's Heart", "Beats slowly, like it’s thinking. May cause sudden attraction, poor decisions, and regret.", 10, 2000, 3);
        InventoryItem distilledWater = new InventoryItem("Distilled Water", "So clean it has no personality. Perfect for balancing chaos, or just diluting your bad ideas.", 10, 29, 3);
        InventoryItem spirit = new InventoryItem("Spirit", "A bottled “presence” that may or may not be judging you. Sometimes it rattles the glass… sometimes you do.", 10, 1750, 3);
        InventoryItem fish = new InventoryItem("Fish", "A tiny orange fish that refuses to get lost, no matter what. Somehow brings luck, bravery, and main-character energy.", 10, 269, 3);
        InventoryItem sirensScales = new InventoryItem("Siren's Scales", "Pretty enough to trust, dangerous enough to regret it. Classic mistake.", 10, 670, 3);

    
        inventory.addItem(lizardTail);
        inventory.addItem(cyclopsEye);
        inventory.addItem(dragonTail);
        inventory.addItem(distilledWater);
        inventory.addItem(tearsOfAChild);
        inventory.addItem(sirensHeart);
        inventory.addItem(spirit);
        inventory.addItem(fish);
        inventory.addItem(sirensScales);

        MenuItem regen = new MenuItem("Potion of Regeneration", "Increases the Player’s HP by 50 and HP regen by 2.", 4279.00, 10);
        regen.addIngredient(sirensHeart, 1);
        regen.addIngredient(distilledWater, 1);
        regen.addIngredient(spirit, 1);

        MenuItem strength = new MenuItem("Potion of Strength", "Increases the Player’s Attack Damage by 10.", 4199.00, 10);
        strength.addIngredient(sirensScales, 1);
        strength.addIngredient(distilledWater, 1);
        strength.addIngredient(dragonTail, 1);
        
        MenuItem waterBreathing= new MenuItem("Potion of Water Breathing", "Allows the Player to breathe underwater for 5 minutes.", 2098.00, 10);
        waterBreathing.addIngredient(distilledWater, 1);
        waterBreathing.addIngredient(lizardTail, 1);
        waterBreathing.addIngredient(tearsOfAChild, 1);
        
        MenuItem teleport = new MenuItem("Potion of Teleportation", "Allows the Player to teleport anywhere within a radius of 100 units..", 2617.00, 10);
        teleport.addIngredient(distilledWater, 1);
        teleport.addIngredient(fish, 1);
        teleport.addIngredient(lizardTail, 1);
        
        MenuItem loveAndAttraction = new MenuItem("Potion of Love and Attraction", "Allows the Player to charm any other Player on the server.", 3529.00, 10);
        loveAndAttraction.addIngredient(distilledWater, 1);
        loveAndAttraction.addIngredient(sirensHeart, 1);
        loveAndAttraction.addIngredient(cyclopsEye, 1);
        
        MenuItem magiciansElixir = new MenuItem("Magician's Elixir", "Boost the person’s overall ability like strength, agility, speed, and mana for 5 minutes.", 9499.00, 10);
        magiciansElixir.addIngredient(distilledWater, 1);
        magiciansElixir.addIngredient(dragonTail, 1);
        magiciansElixir.addIngredient(spirit, 1);
        magiciansElixir.addIngredient(tearsOfAChild, 1);
        magiciansElixir.addIngredient(sirensHeart, 1);
        magiciansElixir.addIngredient(sirensScales, 1);
        
        
        
        menu.addMenuItem(regen);
        menu.addMenuItem(strength);
        menu.addMenuItem(waterBreathing);
        menu.addMenuItem(teleport);
        menu.addMenuItem(loveAndAttraction);
        menu.addMenuItem(magiciansElixir);
    }

    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.validatePassword(password)) {
                return user;
            }
        }

        return null;
    }

    public void addInventoryItem(String itemName, String description, int stocks, double price, int threshold) {
        inventory.addItem(new InventoryItem(itemName, description, stocks, price, threshold));
    }

    public void addMenuItem(MenuItem item) {
        menu.addMenuItem(item);
    }

    public String getBusinessName() {
        return businessName;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Menu getMenu() {
        return menu;
    }
}