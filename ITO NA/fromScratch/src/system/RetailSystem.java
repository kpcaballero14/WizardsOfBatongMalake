package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.CashTransaction;
import model.Cashier;
import model.Inventory;
import model.InventoryItem;
import model.InventoryUsage;
import model.Manager;
import model.Menu;
import model.MenuItem;
import model.SaleTransaction;
import model.User;

public class RetailSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    // Name of the business
    private String businessName;

    // List of users who can log in
    private List<User> users;

    // Inventory object that stores ingredients
    private Inventory inventory;

    // Menu object that stores menu items
    private Menu menu;

    // List of all recorded sales
    private List<SaleTransaction> sales;

    // List of all cash transactions
    private List<CashTransaction> cashTransactions;

    // Current cash balance of the business
    private double cashBalance;

    // Constructor
    public RetailSystem() {
        this.businessName = "Wizards of Batong Malake";
        this.users = new ArrayList<>();
        this.inventory = new Inventory();
        this.menu = new Menu();
        this.sales = new ArrayList<>();
        this.cashTransactions = new ArrayList<>();
        this.cashBalance = 200000;

        // Subtract the initial stock cost from starting capital
        this.cashBalance -= 103000;

        // Record the initial capital
        cashTransactions.add(
            new CashTransaction(
                "INITIAL CAPITAL",
                "Starting business capital",
                200000
            )
        );

        // Record the cost of the initial stock
        cashTransactions.add(
            new CashTransaction(
                "INITIAL STOCK COST",
                "Total Cost of Initial Stock of Ingredients",
                -103000
            )
        );
    }

    // Loads the default users, inventory, and menu items
    public void initializeDefaultData() {
        if (!users.isEmpty()) {
            return;
        }

        users.add(new Manager("manager", "1234", "Project Manager"));
        users.add(new Cashier("cashier", "1234", "Project Cashier"));

        InventoryItem lizardTail = new InventoryItem("Lizard Tail", "Still twitching like it forgot it’s no longer attached.", 10, 70, 3, "images/Lizard_Tail.png");
        InventoryItem cyclopsEye = new InventoryItem("Cyclop's Eye", "Always staring, never blinking.", 10, 1000, 3, "images/Cyclops_Eye.png");
        InventoryItem dragonClaw = new InventoryItem("Dragon's Claw", "A burnt piece that refuses to cool down.", 10, 3000, 3, "images/Dragon_Claw.png");
        InventoryItem tearsOfAChild = new InventoryItem("Tears of a Child", "Pure, innocent, and a little sad.", 10, 1500, 3, "images/Child_Tear.png");
        InventoryItem sirensHeart = new InventoryItem("Siren's Heart", "Beats slowly, like it’s thinking.", 10, 2000, 3, "images/Siren_Heart.png");
        InventoryItem distilledWater = new InventoryItem("Distilled Water", "So clean it has no personality.", 10, 29, 3, "images/Distilled_Water.png");
        InventoryItem spirit = new InventoryItem("Spirit", "A bottled presence that may or may not be judging you.", 10, 1800, 3, "images/Spirit.png");
        InventoryItem fish = new InventoryItem("Fish", "A tiny orange fish that refuses to get lost.", 10, 269, 3, "images/Fish.png");
        InventoryItem sirensScales = new InventoryItem("Siren's Scales", "Pretty enough to trust, dangerous enough to regret it.", 10, 700, 3, "images/Siren_Scale.png");

        inventory.addItem(lizardTail);
        inventory.addItem(cyclopsEye);
        inventory.addItem(dragonClaw);
        inventory.addItem(distilledWater);
        inventory.addItem(tearsOfAChild);
        inventory.addItem(sirensHeart);
        inventory.addItem(spirit);
        inventory.addItem(fish);
        inventory.addItem(sirensScales);

        MenuItem regen = new MenuItem("Potion of Regeneration", "Increases the Player’s HP by 50 and HP regen by 2.", 4300.00, 10, "images/Regen_Potion.png");
        regen.addIngredient(sirensHeart, 1);
        regen.addIngredient(distilledWater, 1);
        regen.addIngredient(spirit, 1);

        MenuItem strength = new MenuItem("Potion of Strength", "Increases the Player’s Attack Damage by 10.", 4200.00, 10, "images/Strength_Potion.png");
        strength.addIngredient(sirensScales, 1);
        strength.addIngredient(distilledWater, 1);
        strength.addIngredient(dragonClaw, 1);

        MenuItem waterBreathing = new MenuItem("Potion of Water Breathing", "Allows the Player to breathe underwater for 5 minutes.", 2100.00, 10, "images/Water_Breathing_Potion.png");
        waterBreathing.addIngredient(distilledWater, 1);
        waterBreathing.addIngredient(lizardTail, 1);
        waterBreathing.addIngredient(tearsOfAChild, 1);

        MenuItem teleport = new MenuItem("Potion of Teleportation", "Allows the Player to teleport anywhere within a radius of 100 units.", 2600.00, 10, "images/Teleportation_Potion.png");
        teleport.addIngredient(distilledWater, 1);
        teleport.addIngredient(fish, 1);
        teleport.addIngredient(lizardTail, 1);

        MenuItem loveAndAttraction = new MenuItem("Potion of Love", "Allows the Player to charm any other Player on the server.", 3600.00, 10, "images/Love_Potion.png");
        loveAndAttraction.addIngredient(distilledWater, 1);
        loveAndAttraction.addIngredient(sirensHeart, 1);
        loveAndAttraction.addIngredient(cyclopsEye, 1);

        MenuItem magiciansElixir = new MenuItem("Magician's Elixir", "Boosts strength, agility, speed, and mana for 5 minutes.", 9500.00, 10, "images/Magicians_Elixir.png");
        magiciansElixir.addIngredient(distilledWater, 1);
        magiciansElixir.addIngredient(dragonClaw, 1);
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

        // Load saved data if it exists
        if (DataStore.exists()) {
            loadFromTextFile();
        } else {
            saveToTextFile();
        }
    }

    // Processes a sale of a menu item
    public SaleTransaction processSale(MenuItem menuItem, int quantity, double amountPaid) {
        if (menuItem == null || quantity <= 0) {
            return null;
        }

        double total = menuItem.getPrice() * quantity;

        // Check if payment is enough
        if (amountPaid < total) {
            return null;
        }

        // Check if all ingredients have enough stock
        for (InventoryUsage usage : menuItem.getIngredients()) {
            int needed = usage.getQuantityNeeded() * quantity;
            if (usage.getInventoryItem().getStocks() < needed) {
                return null;
            }
        }

        // Deduct ingredient stocks
        for (InventoryUsage usage : menuItem.getIngredients()) {
            int needed = usage.getQuantityNeeded() * quantity;
            usage.getInventoryItem().consumeStock(needed);
        }

        // Create a sale record
        SaleTransaction sale = new SaleTransaction(
            menuItem.getItemName(),
            quantity,
            menuItem.getPrice(),
            amountPaid
        );

        sales.add(sale);

        // Record the sale in cash transactions
        CashTransaction cashTransaction = new CashTransaction(
            "SALE",
            "Sale of " + quantity + " " + menuItem.getItemName(),
            sale.getTotalAmount()
        );

        cashTransactions.add(cashTransaction);
        cashBalance += sale.getTotalAmount();

        return sale;
    }

    // Adds money into the cash balance
    public void addCashIn(String description, double amount) {
        if (amount > 0) {
            cashTransactions.add(new CashTransaction("CASH IN", description, amount));
            cashBalance += amount;
            saveToTextFile();
        }
    }

    // Deducts money from the cash balance
    public void addCashOut(String description, double amount) {
        if (amount > 0 && cashBalance >= amount) {
            cashTransactions.add(new CashTransaction("CASH OUT", description, amount));
            cashBalance -= amount;
            saveToTextFile();
        }
    }

    // Saves the current state through DataStore
    public void saveToTextFile() {
        DataStore.save(this);
    }

    // Loads the saved state through DataStore
    private void loadFromTextFile() {
        DataStore.load(this);
    }

    // Restocks an inventory item and records its cost
    public boolean restockInventoryItem(InventoryItem item, int amount) {
        if (item == null || amount <= 0) {
            return false;
        }

        double totalCost = item.getPrice() * amount;

        // Stop if there is not enough cash balance
        if (cashBalance < totalCost) {
            return false;
        }

        item.addStock(amount);
        cashBalance -= totalCost;

        // Record the restock transaction
        cashTransactions.add(new CashTransaction(
                "RESTOCK",
                "Restocked " + amount + " unit/s of " + item.getItemName(),
                totalCost
        ));

        saveToTextFile();
        return true;
    }

    // Allows DataStore to update cash balance while loading
    void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    // Returns the list of sales
    public List<SaleTransaction> getSales() {
        return sales;
    }

    // Returns the list of cash transactions
    public List<CashTransaction> getCashTransactions() {
        return cashTransactions;
    }

    // Returns the current cash balance
    public double getCashBalance() {
        return cashBalance;
    }

    // Checks login credentials
    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.validatePassword(password)) {
                return user;
            }
        }
        return null;
    }

    // Adds a new inventory item with image
    public void addInventoryItem(String itemName, String description, int stocks, double price, int threshold, String imagePath) {
        inventory.addItem(new InventoryItem(itemName, description, stocks, price, threshold, imagePath));
        saveToTextFile();
    }

    // Adds a new inventory item without image
    public void addInventoryItem(String itemName, String description, int stocks, double price, int threshold) {
        inventory.addItem(new InventoryItem(itemName, description, stocks, price, threshold, ""));
        saveToTextFile();
    }

    // Adds a new menu item
    public void addMenuItem(MenuItem item) {
        menu.addMenuItem(item);
        saveToTextFile();
    }

    // Returns the business name
    public String getBusinessName() {
        return businessName;
    }

    // Returns the inventory object
    public Inventory getInventory() {
        return inventory;
    }

    // Returns the menu object
    public Menu getMenu() {
        return menu;
    }
}