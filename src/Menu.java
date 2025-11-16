// Menu.java
public class Menu {

    private String name;
    private double price;
    private String category; // food/drink

    // Constructor
    public Menu(String name, double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    // --- Getters (Indicator 1: Method) ---
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    // --- Setter (Method for Menu Management) ---
    public void setPrice(double price) {
        // Simple validation, price cannot be negative
        if (price > 0) {
            this.price = price;
        }
    }
}