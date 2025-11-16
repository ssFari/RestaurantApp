// Order.java
public class Order {

    private Menu menu;
    private int quantity;

    public Order(Menu menu, int quantity) {
        this.menu = menu;
        this.quantity = quantity;
    }

    // --- Getters ---
    public Menu getMenu() {
        return menu;
    }

    public int getQuantity() {
        return quantity;
    }

    // Method to add quantity if the same item is ordered again
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    // Helper method to calculate the subtotal per item
    public double getSubtotal() {
        return menu.getPrice() * quantity;
    }
}
