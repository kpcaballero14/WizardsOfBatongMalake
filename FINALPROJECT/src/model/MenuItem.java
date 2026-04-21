package model;

public class MenuItem {
    private String itemName;
    private String description;
    private double price;
    private double profitPercentage;

    public MenuItem(String itemName, String description, double price){
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.profitPercentage = ((price-(price-500))/price)*100;
    }

  public String getItemName(){return itemName;}
  public String getDescription(){return description;}
  public double getPrice(){return price;}
  public double getProfitPercentage(){return profitPercentage;}
}
