package system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import model.CashTransaction;
import model.InventoryItem;
import model.MenuItem;
import model.SaleTransaction;

public class DataStore {
    // File path of the text file used for saving and loading data
    private static final Path DATA_FILE = Paths.get("data", "retail_system_state.txt");

    // Checks if the save file already exists
    public static boolean exists() {
        return Files.exists(DATA_FILE);
    }

    // Saves the current RetailSystem data into the text file
    public static void save(RetailSystem system) {
        try {
            // Create the data folder if it does not exist
            Files.createDirectories(DATA_FILE.getParent());

            // Open the file for writing
            try (BufferedWriter writer = Files.newBufferedWriter(DATA_FILE, StandardCharsets.UTF_8)) {
                writer.write("# Wizards of Batong Malake Retail System State");
                writer.newLine();

                // Save the cash balance
                writer.write("CASH_BALANCE\t" + system.getCashBalance());
                writer.newLine();

                // Save inventory data
                writer.write("[INVENTORY]");
                writer.newLine();
                for (InventoryItem item : system.getInventory().getItems()) {
                    writer.write(
                        escape(item.getItemName()) + "\t" +
                        item.getStocks() + "\t" +
                        item.getUsedCount()
                    );
                    writer.newLine();
                }

                // Save menu data
                writer.write("[MENU]");
                writer.newLine();
                for (MenuItem item : system.getMenu().getItems()) {
                    writer.write(
                        escape(item.getItemName()) + "\t" +
                        item.getProfitPercentage()
                    );
                    writer.newLine();
                }

                // Save sales data
                writer.write("[SALES]");
                writer.newLine();
                for (SaleTransaction sale : system.getSales()) {
                    writer.write(
                        escape(sale.getItemName()) + "\t" +
                        sale.getQuantity() + "\t" +
                        sale.getUnitPrice() + "\t" +
                        sale.getAmountPaid() + "\t" +
                        sale.getDateTime()
                    );
                    writer.newLine();
                }

                // Save cash transaction data
                writer.write("[CASH_TRANSACTIONS]");
                writer.newLine();
                for (CashTransaction transaction : system.getCashTransactions()) {
                    writer.write(
                        escape(transaction.getType()) + "\t" +
                        escape(transaction.getDescription()) + "\t" +
                        transaction.getAmount() + "\t" +
                        transaction.getDateTime()
                    );
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to save data to text file.");
            e.printStackTrace();
        }
    }

    // Loads the saved data from the text file into the RetailSystem object
    public static void load(RetailSystem system) {
        // Clear old sales and cash transaction data first
        system.getSales().clear();
        system.getCashTransactions().clear();

        try (BufferedReader reader = Files.newBufferedReader(DATA_FILE, StandardCharsets.UTF_8)) {
            String line;
            String section = "";

            while ((line = reader.readLine()) != null) {
                // Skip empty lines and comment lines
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                // Load cash balance
                if (line.startsWith("CASH_BALANCE\t")) {
                    String[] parts = line.split("\t", -1);
                    if (parts.length >= 2) {
                        system.setCashBalance(Double.parseDouble(parts[1]));
                    }
                    continue;
                }

                // Detect current section
                if (line.startsWith("[") && line.endsWith("]")) {
                    section = line;
                    continue;
                }

                String[] parts = line.split("\t", -1);

                switch (section) {
                    case "[INVENTORY]":
                        if (parts.length >= 3) {
                            String itemName = unescape(parts[0]);
                            int stocks = Integer.parseInt(parts[1]);
                            int usedCount = Integer.parseInt(parts[2]);

                            InventoryItem item = findInventoryItemByName(system, itemName);
                            if (item != null) {
                                item.setStocks(stocks);
                                item.setUsedCount(usedCount);
                            }
                        }
                        break;

                    case "[MENU]":
                        if (parts.length >= 2) {
                            String itemName = unescape(parts[0]);
                            double profit = Double.parseDouble(parts[1]);

                            MenuItem item = findMenuItemByName(system, itemName);
                            if (item != null) {
                                item.setProfitPercentage(profit);
                            }
                        }
                        break;

                    case "[SALES]":
                        if (parts.length >= 5) {
                            String itemName = unescape(parts[0]);
                            int quantity = Integer.parseInt(parts[1]);
                            double unitPrice = Double.parseDouble(parts[2]);
                            double amountPaid = Double.parseDouble(parts[3]);
                            LocalDateTime dateTime = LocalDateTime.parse(parts[4]);

                            system.getSales().add(
                                new SaleTransaction(itemName, quantity, unitPrice, amountPaid, dateTime)
                            );
                        }
                        break;

                    case "[CASH_TRANSACTIONS]":
                        if (parts.length >= 4) {
                            String type = unescape(parts[0]);
                            String description = unescape(parts[1]);
                            double amount = Double.parseDouble(parts[2]);
                            LocalDateTime dateTime = LocalDateTime.parse(parts[3]);

                            system.getCashTransactions().add(
                                new CashTransaction(type, description, amount, dateTime)
                            );
                        }
                        break;

                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load data from text file.");
            e.printStackTrace();
        }
    }

    // Finds an inventory item by name
    private static InventoryItem findInventoryItemByName(RetailSystem system, String itemName) {
        for (InventoryItem item : system.getInventory().getItems()) {
            if (item.getItemName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    // Finds a menu item by name
    private static MenuItem findMenuItemByName(RetailSystem system, String itemName) {
        for (MenuItem item : system.getMenu().getItems()) {
            if (item.getItemName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    // Escapes special characters before saving text
    private static String escape(String value) {
        return value
            .replace("\\", "\\\\")
            .replace("\t", "\\t")
            .replace("\n", "\\n")
            .replace("\r", "");
    }

    // Restores escaped characters after loading text
    private static String unescape(String value) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);

            if (current == '\\' && i + 1 < value.length()) {
                char next = value.charAt(i + 1);

                if (next == 't') {
                    result.append('\t');
                    i++;
                } else if (next == 'n') {
                    result.append('\n');
                    i++;
                } else if (next == '\\') {
                    result.append('\\');
                    i++;
                } else {
                    result.append(current);
                }
            } else {
                result.append(current);
            }
        }

        return result.toString();
    }
}