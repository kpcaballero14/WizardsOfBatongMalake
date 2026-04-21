package model;

public class MenuItem {
    private String itemName;
    private String description;
    private int price;

    public MenuItem(String itemName, String description, int price){
        this.itemName = itemName;
        this.description = description;
        this.price = price;
    }

  public String getItemName(){return itemName;}
  public String getDescription(){return description;}
  public int getPrice(){return price;}
}
