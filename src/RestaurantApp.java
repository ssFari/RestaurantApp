
// RestaurantApp.java
import java.util.ArrayList; // Using ArrayList (dynamic array) to add/remove
import java.util.Scanner;

public class RestaurantApp {

    // --- Indicator 1 & 2: Data Types, Variables, Keywords, Class, Array ---
    // Using ArrayList so menu data can be dynamic (Add/Edit/Delete)
    private static ArrayList<Menu> menuList = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    // Final variables for fixed costs
    private static final double TAX_PERCENTAGE = 0.10; // 10%
    private static final double SERVICE_CHARGE = 20000.0;

    public static void main(String[] args) {
        // Initialize default menu data (Minimum 4 per category)
        initializeMenu();

        // --- Indicator 4: Loop Structure (while) ---
        // Main application loop
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n===== WELCOME TO THE RESTAURANT =====");
            System.out.println("1. Customer Menu (Order Food)");
            System.out.println("2. Management Menu (Admin)");
            System.out.println("3. Exit");
            System.out.print("Select menu (1-3): ");

            int choice = getNumericInput();

            // --- Indicator 3: Decision Structure (switch case) ---
            switch (choice) {
                case 1:
                    customerMenu();
                    break;
                case 2:
                    managementMenu();
                    break;
                case 3:
                    isRunning = false;
                    System.out.println("Thank you for visiting!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    /**
     * Helper to initialize default menu data
     */
    private static void initializeMenu() {
        // Food Category
        menuList.add(new Menu("Nasi Padang", 25000.0, "Food"));
        menuList.add(new Menu("Ayam Geprek", 20000.0, "Food"));
        menuList.add(new Menu("Mie Goreng", 18000.0, "Food"));
        menuList.add(new Menu("Sate Ayam", 22000.0, "Food"));

        // Drink Category
        menuList.add(new Menu("Es Teh Manis", 5000.0, "Drink"));
        menuList.add(new Menu("Kopi Susu", 15000.0, "Drink"));
        menuList.add(new Menu("Lemon Tea", 12000.0, "Drink"));
        menuList.add(new Menu("Jus Alpukat", 15000.0, "Drink"));
    }

    // ===============================================
    // --- CUSTOMER MENU SECTION (Tasks 1-4) ---
    // ===============================================

    /**
     * Main logic for the customer flow
     */
    private static void customerMenu() {
        System.out.println("\n--- Customer Menu ---");
        displayMenu(false); // Display menu without numbers

        // 1. Input Orders (Indicator 4: Loop)
        ArrayList<Order> customerOrders = inputOrders();

        if (customerOrders.isEmpty()) {
            System.out.println("You have not ordered anything.");
            return;
        }

        // 2. Calculate Total Cost (Indicator 3 & 4: Decision & Loop)
        // 1D array to store calculation results [totalPrice, discount, tax, finalTotal]
        double[] calculationResults = calculateTotal(customerOrders);

        // 3. Print Receipt
        printReceipt(customerOrders, calculationResults);
    }

    /**
     * Displays the menu list, grouped by category
     * 
     * @param showNumbers boolean for admin mode (true) or customer mode (false)
     */
    private static void displayMenu(boolean showNumbers) {
        System.out.println("\n============== RESTAURANT MENU ==============");

        // --- Indicator 4: Loop Structure (for-each) ---
        // Food Category
        System.out.println("--- Food ---");
        for (Menu item : menuList) {
            if (item.getCategory().equalsIgnoreCase("Food")) {
                if (showNumbers) {
                    // Find the original index for the correct number
                    int index = menuList.indexOf(item) + 1;
                    System.out.printf("%d. %-20s (Rp %d)\n", index, item.getName(), (int) item.getPrice());
                } else {
                    System.out.printf("- %-20s (Rp %d)\n", item.getName(), (int) item.getPrice());
                }
            }
        }

        // Drink Category
        System.out.println("\n--- Drinks ---");
        for (Menu item : menuList) {
            if (item.getCategory().equalsIgnoreCase("Drink")) {
                if (showNumbers) {
                    // Find the original index for the correct number
                    int index = menuList.indexOf(item) + 1;
                    System.out.printf("%d. %-20s (Rp %d)\n", index, item.getName(), (int) item.getPrice());
                } else {
                    System.out.printf("- %-20s (Rp %d)\n", item.getName(), (int) item.getPrice());
                }
            }
        }
        System.out.println("===========================================");
    }

    /**
     * Logic for customer order input (Task 2)
     * Uses a while(true) loop and validation
     */
    private static ArrayList<Order> inputOrders() {
        ArrayList<Order> cart = new ArrayList<>();

        // --- Indicator 4: Loop Structure (while) ---
        while (true) {
            System.out.print("\nEnter order (Menu Name) [type 'done' to stop]: ");
            String menuName = scanner.nextLine();

            if (menuName.equalsIgnoreCase("done")) {
                break; // Exit the ordering loop
            }

            // Validate menu input (Task 2 Note)
            Menu orderedMenu = findMenuByName(menuName);

            if (orderedMenu == null) {
                System.out.println("Menu '" + menuName + "' not found. Please input again.");
                continue; // Continue to the next loop iteration
            }

            System.out.print("Quantity: ");
            int quantity = getNumericInput();

            if (quantity <= 0) {
                System.out.println("Quantity must be more than 0.");
                continue;
            }

            // Check if the menu is already in the cart
            boolean menuExistsInCart = false;
            for (Order o : cart) {
                if (o.getMenu().getName().equalsIgnoreCase(menuName)) {
                    o.addQuantity(quantity);
                    menuExistsInCart = true;
                    break;
                }
            }

            if (!menuExistsInCart) { // if not found, add new order
                cart.add(new Order(orderedMenu, quantity));
            }
            System.out.println("Successfully added " + quantity + " " + orderedMenu.getName() + " to cart.");
        }
        return cart;
    }

    /**
     * Logic to calculate total cost, discount, tax (Task 3)
     */
    private static double[] calculateTotal(ArrayList<Order> cart) {
        double totalBeforeDiscount = 0.0;
        double discount = 0.0;
        double tax = 0.0;
        double finalTotal = 0.0;
        double cheapestDrinkOrdered = Double.MAX_VALUE;
        String discountInfo = "No discount";

        // --- Indicator 4: Loop (for-each) to calculate initial total ---
        for (Order o : cart) {
            totalBeforeDiscount += o.getSubtotal();

            // Find the cheapest drink ordered for the B1G1 promo
            if (o.getMenu().getCategory().equalsIgnoreCase("Drink")) {
                if (o.getMenu().getPrice() < cheapestDrinkOrdered) {
                    cheapestDrinkOrdered = o.getMenu().getPrice();
                }
            }
        }

        // --- Indicator 3: Decision Structure (if-else if / nested if) ---
        // Scenario 1: 10% Discount
        if (totalBeforeDiscount > 100000.0) {
            discount = totalBeforeDiscount * 0.10;
            discountInfo = "Discount 10% (Total > Rp 100,000)";
        }
        // Scenario 2: Buy 1 Get 1 Drink
        else if (totalBeforeDiscount > 50000.0) {
            // Nested If: Check if the customer bought a drink
            if (cheapestDrinkOrdered != Double.MAX_VALUE) {
                discount = cheapestDrinkOrdered;
                discountInfo = "B1G1 Drink Promo (Free Rp " + (int) discount + ")";
            }
        }
        // Scenario 3: No discount (default)

        double subTotalAfterDiscount = totalBeforeDiscount - discount;
        tax = subTotalAfterDiscount * TAX_PERCENTAGE;
        finalTotal = subTotalAfterDiscount + tax + SERVICE_CHARGE;

        // Store discount info for the receipt
        System.setProperty("discountInfo", discountInfo);

        return new double[] { totalBeforeDiscount, discount, tax, finalTotal };
    }

    /**
     * Logic to print the receipt (Task 4)
     */
    private static void printReceipt(ArrayList<Order> cart, double[] results) {
        double totalBeforeDiscount = results[0];
        double discount = results[1];
        double tax = results[2];
        double finalTotal = results[3];
        String discountInfo = System.getProperty("discountInfo");

        System.out.println("\n\n============ PAYMENT RECEIPT ============");
        System.out.printf("%-15s %-5s %-10s %-10s\n", "ITEM", "QTY", "PRICE", "TOTAL");
        System.out.println("------------------------------------------");

        // --- Indicator 4: Loop (for-each) ---
        for (Order o : cart) {
            Menu m = o.getMenu();
            System.out.printf("%-15s %-5d Rp %-8d Rp %-10.2f\n",
                    m.getName(), o.getQuantity(), (int) m.getPrice(), o.getSubtotal());
        }

        System.out.println("------------------------------------------");
        System.out.printf("%-30s Rp %-10.2f\n", "Initial Total", totalBeforeDiscount);

        // --- Indicator 3: Decision (if) ---
        if (discount > 0.0) {
            System.out.printf("%-30s -Rp %-10.2f\n", discountInfo, discount);
            System.out.printf("%-30s Rp %-10.2f\n", "Subtotal After Discount", (totalBeforeDiscount - discount));
        }

        System.out.printf("%-30s +Rp %-10.2f\n", "Tax (10%)", tax);
        System.out.printf("%-30s +Rp %-10.2f\n", "Service Charge", SERVICE_CHARGE);
        System.out.println("------------------------------------------");
        System.out.printf("%-30s Rp %-10.2f\n", "TOTAL TO PAY", finalTotal);
        System.out.println("==========================================");
    }

    // ===============================================
    // --- MANAGEMENT MENU SECTION (Task 5) ---
    // ===============================================

    /**
     * Main logic for admin menu (Add, Edit, Delete)
     */
    private static void managementMenu() {
        // --- Indicator 4: Loop (do-while) ---
        boolean goBack = false;
        do {
            System.out.println("\n--- Management Menu (Admin) ---");
            System.out.println("1. Add New Menu");
            System.out.println("2. Update Menu Price");
            System.out.println("3. Delete Menu");
            System.out.println("4. View Menu List");
            System.out.println("5. Back to Main Menu");
            System.out.print("Select menu (1-5): ");

            int choice = getNumericInput();

            // --- Indicator 3: Decision (switch) ---
            switch (choice) {
                case 1:
                    addMenu();
                    break;
                case 2:
                    updateMenu();
                    break;
                case 3:
                    deleteMenu();
                    break;
                case 4:
                    displayMenu(true); // Display with numbers
                    break;
                case 5:
                    goBack = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (!goBack);
    }

    /**
     * Logic to add a new menu item
     */
    private static void addMenu() {
        // --- Indicator 4: Loop (do-while) ---
        String addAnother;
        do {
            System.out.println("\n--- Add New Menu ---");
            System.out.print("Enter Menu Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Menu Price: ");
            double price = getPriceInput(); // Validate price input

            System.out.print("Enter Category (Food/Drink): ");
            String category = scanner.nextLine();

            // Category validation
            while (!category.equalsIgnoreCase("Food") && !category.equalsIgnoreCase("Drink")) {
                System.out.print("Invalid category. Please enter 'Food' or 'Drink': ");
                category = scanner.nextLine();
            }

            menuList.add(new Menu(name, price, category));
            System.out.println("Successfully added menu: " + name);

            System.out.print("Add another menu? (Yes/No): ");
            addAnother = scanner.nextLine();
        } while (addAnother.equalsIgnoreCase("Yes"));
    }

    /**
     * Logic to update a menu item's price
     */
    private static void updateMenu() {
        System.out.println("\n--- Update Menu Price ---");
        displayMenu(true); // Display menu list with numbers
        System.out.print("Enter the Menu Number to update: ");
        int number = getNumericInput();

        // Validate number (Index = number - 1)
        if (number > 0 && number <= menuList.size()) {
            int index = number - 1;
            Menu menu = menuList.get(index);

            System.out.println("Selected Menu: " + menu.getName() + " (Old Price: Rp " + (int) menu.getPrice() + ")");
            System.out.print("Enter New Price: ");
            double newPrice = getPriceInput();

            System.out.print("Are you sure you want to change the price? (Yes/No): ");
            String confirmation = scanner.nextLine();

            // --- Indicator 3: Decision (if) ---
            if (confirmation.equalsIgnoreCase("Yes")) {
                menu.setPrice(newPrice); // Using the setPrice method
                System.out.println("Price updated successfully.");
            } else {
                System.out.println("Update cancelled.");
            }
        } else {
            System.out.println("Invalid menu number.");
        }
    }

    /**
     * Logic to delete a menu item
     */
    private static void deleteMenu() {
        System.out.println("\n--- Delete Menu ---");
        displayMenu(true); // Display menu list with numbers
        System.out.print("Enter the Menu Number to delete: ");
        int number = getNumericInput();

        // Validate number (Index = number - 1)
        if (number > 0 && number <= menuList.size()) {
            int index = number - 1;
            Menu menu = menuList.get(index);

            System.out.print("ARE YOU SURE you want to delete " + menu.getName() + "? (Yes/No): ");
            String confirmation = scanner.nextLine();

            // --- Indicator 3: Decision (if) ---
            if (confirmation.equalsIgnoreCase("Yes")) {
                menuList.remove(index);
                System.out.println("Menu deleted successfully.");
            } else {
                System.out.println("Deletion cancelled.");
            }
        } else {
            System.out.println("Invalid menu number.");
        }
    }

    // ===============================================
    // --- HELPER METHODS (Input Validation) ---
    // ===============================================

    /**
     * Helper to find a Menu object by its name
     * 
     * @return Menu object if found, null if not
     */
    private static Menu findMenuByName(String name) {
        // --- Indicator 4: Loop (for-each) ---
        for (Menu item : menuList) {
            // --- Indicator 2: String method ---
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Helper to validate numeric input (prevents errors)
     */
    private static int getNumericInput() {
        // --- Indicator 4: Loop (while) ---
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input); // Try to convert to number
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    /**
     * Helper to validate price input (prevents errors)
     */
    private static double getPriceInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                double price = Double.parseDouble(input);
                if (price <= 0) {
                    System.out.print("Price must be greater than 0. Re-enter: ");
                    continue;
                }
                return price;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number (e.g.: 25000): ");
            }
        }
    }
}