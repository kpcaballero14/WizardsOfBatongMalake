package model;

public class InventoryItem {
		private String itemName;
		private String description;
		private int stocks;
		private int price;
		private int ingredientUsage;

		public InnventoryItem(String itemName, String description, int stocks, int price){
				this.itemName = itemName;
				this.description = description;
				this.stocks = stocks;
				this.price = price;
				this.ingredientUsage = 0;
		}

		public String getItemName(){return itemName;}
		public String description(){return description;}
		public int getStocks(){return stocks;}
		public int getPrice(){return price;}
		public int getIngredientsUsage(){return ingredientsUsage;}
}
