import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.Iterator;

// Class representing the information of an item
class ItemInformation {
    private int itemId;
    private String itemName;
    private double itemPrice;
    private String datePurchase;

    public ItemInformation(int itemId, String itemName, double itemPrice, String datePurchase) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.datePurchase = datePurchase;
    }

    // Getters and setters for item information
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getDatePurchase() {
        return datePurchase;
    }

    public void setDatePurchase(String datePurchase) {
        this.datePurchase = datePurchase;
    }
}

// Class representing the information of a customer
class CustomerInformation {
    private int custId;
    private String custIC;
    private double counterPaid;
    private List<ItemInformation> purchasedItems;

    public CustomerInformation(int custId, String custIC, double counterPaid) {
        this.custId = custId;
        this.custIC = custIC;
        this.counterPaid = counterPaid;
        this.purchasedItems = new ArrayList<>();
    }

    // Getters and setters for customer information
    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public String getCustIC() {
        return custIC;
    }

    public void setCustIC(String custIC) {
        this.custIC = custIC;
    }

    public double getCounterPaid() {
        return counterPaid;
    }

    public void setCounterPaid(double counterPaid) {
        this.counterPaid = counterPaid;
    }

    public List<ItemInformation> getPurchasedItems() {
        return purchasedItems;
    }

    public void setPurchasedItems(List<ItemInformation> purchasedItems) {
        this.purchasedItems = purchasedItems;
    }

    public void addItem(ItemInformation item) {
        purchasedItems.add(item);
    }
}

public class HypermarketSystem {
    private static final int MAX_ITEMS_COUNTER1 = 5;
    private static final int MAX_ITEMS_COUNTER2 = 5;
    private static final int COUNTER3_THRESHOLD = 5;

    private Queue<CustomerInformation> counter1Queue; // Queue for counter 1
    private Queue<CustomerInformation> counter2Queue; // Queue for counter 2
    private Queue<CustomerInformation> counter3Queue; // Queue for counter 3
    private Stack<CustomerInformation> completeStack; // Stack to store completed customers

    private String customerDataFilePath; // Path to the customer data file

    public HypermarketSystem(String customerDataFilePath) {
        this.customerDataFilePath = customerDataFilePath;
        counter1Queue = new LinkedList<>();
        counter2Queue = new LinkedList<>();
        counter3Queue = new LinkedList<>();
        completeStack = new Stack<>();
    }

    // Add a customer to the system
    public void addCustomer(CustomerInformation customer) {
        if (customer.getPurchasedItems().size() <= MAX_ITEMS_COUNTER1) {
            counter1Queue.offer(customer); // Add to counter 1 queue
        } else if (customer.getPurchasedItems().size() <= MAX_ITEMS_COUNTER2) {
            counter2Queue.offer(customer); // Add to counter 2 queue
        } else {
            counter3Queue.offer(customer); // Add to counter 3 queue
        }
        completeStack.push(customer); // Add to completeStack
    }

    // Remove a customer from the system
    public void removeCustomer(int customerId) {
        boolean removed = false;
        removed = removeCustomerFromQueue(counter1Queue, customerId);
        if (!removed) {
            removed = removeCustomerFromQueue(counter2Queue, customerId);
        }
        if (!removed) {
            removed = removeCustomerFromQueue(counter3Queue, customerId);
        }
        if (!removed) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
        } else {
            System.out.println("Customer with ID " + customerId + " removed successfully.");
        }
    }

    private boolean removeCustomerFromQueue(Queue<CustomerInformation> queue, int customerId) {
        Iterator<CustomerInformation> iterator = queue.iterator();
        while (iterator.hasNext()) {
            CustomerInformation customer = iterator.next();
            if (customer.getCustId() == customerId) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }


    // Read customer data from a file
    private List<CustomerInformation> readCustomerDataFromFile() {
        List<CustomerInformation> customerList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(customerDataFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                int custId = Integer.parseInt(data[0]);
                String custIC = data[1];
                double counterPaid = Double.parseDouble(data[2]);

                CustomerInformation customer = new CustomerInformation(custId, custIC, counterPaid);

                for (int i = 3; i < data.length; i += 4) {
                    int itemId = Integer.parseInt(data[i]);
                    String itemName = data[i + 1];
                    double itemPrice = Double.parseDouble(data[i + 2]);
                    String datePurchase = data[i + 3];

                    ItemInformation item = new ItemInformation(itemId, itemName, itemPrice, datePurchase);
                    customer.addItem(item);
                }

                customerList.add(customer);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return customerList;
    }

    // Update the customer data file with the remaining customers
    private void updateCustomerDataFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerDataFilePath))) {
            for (CustomerInformation customer : completeStack) {
                StringBuilder line = new StringBuilder();
                line.append(customer.getCustId()).append(",");
                line.append(customer.getCustIC()).append(",");
                line.append(customer.getCounterPaid());

                for (ItemInformation item : customer.getPurchasedItems()) {
                    line.append(",");
                    line.append(item.getItemId()).append(",");
                    line.append(item.getItemName()).append(",");
                    line.append(item.getItemPrice()).append(",");
                    line.append(item.getDatePurchase());
                }

                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Display details of all customers in the system
    public void displayCustomers() {
        System.out.println("Displaying customer details:");
        displayCustomersFromQueue(counter1Queue);
        displayCustomersFromQueue(counter2Queue);
        displayCustomersFromQueue(counter3Queue);
    }

    // Process all customers in the system
    private void processCustomers() {
        while (!counter1Queue.isEmpty() || !counter2Queue.isEmpty() || !counter3Queue.isEmpty()) {
            processCustomer(counter1Queue); // Process customers at counter 1
            processCustomer(counter2Queue); // Process customers at counter 2
            processCustomer(counter3Queue); // Process customers at counter 3
        }
    }

    // Process a customer from a specific counter
    private void processCustomer(Queue<CustomerInformation> counterQueue) {
        if (!counterQueue.isEmpty()) {
            CustomerInformation customer = counterQueue.poll();
            completeStack.push(customer);
            System.out.println("Processing customer ID: " + customer.getCustId());
            displayCustomerDetails(customer);
            double totalAmountPaid = calculateTotalAmountPaid(customer);
            System.out.println("Total amount paid: $" + totalAmountPaid);
            System.out.println("---------------------------------------");
        }
    }

    // Display details of a customer
    private void displayCustomerDetails(CustomerInformation customer) {
        System.out.println("Customer ID: " + customer.getCustId());
        System.out.println("IC Number: " + customer.getCustIC());
        System.out.println("Items Purchased:");
        for (ItemInformation item : customer.getPurchasedItems()) {
            System.out.println("  - Item ID: " + item.getItemId());
            System.out.println("    Item Name: " + item.getItemName());
            System.out.println("    Item Price: " + item.getItemPrice());
            System.out.println("    Date Purchased: " + item.getDatePurchase());
        }
    }

    // Calculate the total amount paid by a customer
    private double calculateTotalAmountPaid(CustomerInformation customer) {
        double totalAmountPaid = 0.0;
        for (ItemInformation item : customer.getPurchasedItems()) {
            totalAmountPaid += item.getItemPrice();
        }
        return totalAmountPaid;
    }

    // Display details of customers from a specific queue
    private void displayCustomersFromQueue(Queue<CustomerInformation> queue) {
        for (CustomerInformation customer : queue) {
            displayCustomerDetails(customer);
            double totalAmountPaid = calculateTotalAmountPaid(customer);
            System.out.println("Total amount paid: $" + totalAmountPaid);
            System.out.println("---------------------------------------");
        }
    }

    // Main method
    public static void main(String[] args) {
        String customerDataFilePath = "customer_data.txt"; // Path to the customer data file
        HypermarketSystem system = new HypermarketSystem(customerDataFilePath);
        List<CustomerInformation> customers = system.readCustomerDataFromFile();

        for (CustomerInformation customer : customers) {
            system.addCustomer(customer);
        }

        boolean exit = false;
        Scanner scanner = new Scanner(System.in);

        while (!exit) {
            System.out.println("Hypermarket System Menu");
            System.out.println("1. Add Customer");
            System.out.println("2. Remove Customer");
            System.out.println("3. Display Customers");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.println("Enter customer details:");
                    System.out.print("Customer ID: ");
                    int custId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    System.out.print("IC Number: ");
                    String custIC = scanner.nextLine();
                    System.out.print("Counter Paid: ");
                    double counterPaid = scanner.nextDouble();
                    scanner.nextLine(); // Consume the newline character

                    CustomerInformation customer = new CustomerInformation(custId, custIC, counterPaid);
                    boolean addItem = true;
                    while (addItem) {
                        System.out.println("Enter item details (Enter 'N' to stop):");
                        System.out.print("Item ID: ");
                        String itemIdInput = scanner.nextLine();
                        if (itemIdInput.equalsIgnoreCase("N")) {
                            addItem = false;
                        } else {
                            int itemId = Integer.parseInt(itemIdInput);
                            System.out.print("Item Name: ");
                            String itemName = scanner.nextLine();
                            System.out.print("Item Price: ");
                            double itemPrice = scanner.nextDouble();
                            scanner.nextLine(); // Consume the newline character
                            System.out.print("Date Purchased: ");
                            String datePurchase = scanner.nextLine();

                            ItemInformation item = new ItemInformation(itemId, itemName, itemPrice, datePurchase);
                            customer.addItem(item);
                        }
                    }

                    system.addCustomer(customer);
                    break;

                case 2:
                    System.out.print("Enter customer ID to remove: ");
                    int customerId = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    system.removeCustomer(customerId);
                    break;

                case 3:
                    system.displayCustomers();
                    break;

                case 4:
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
    }
}