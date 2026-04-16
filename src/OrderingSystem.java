import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

enum ShippingType {STANDARD, NEXT_DAY}

public class OrderingSystem
{
  private String Name="";
  private String State="";
  private ShippingType shippingType=ShippingType.STANDARD;

  private final ArrayList<Product> catalog;
  private final ShoppingCart shoppingCart;

  private Product GetSelectedProduct() {
    System.out.println("\nEnter the number of the product you want to order:\n ");
    for (int i = 0; i < catalog.size(); i++) {
      System.out.println((i+1)+") " + catalog.get(i).name + " " +
              String.format("%,.2f",  catalog.get(i).price));
    }
    int selection =0;
    while (selection > catalog.size() || selection <=0)     {
      Scanner scan = new Scanner(System.in);
      String line = scan.nextLine();
      selection = Integer.parseInt(line);
      if (selection > catalog.size() || selection <=0)
        System.out.println("\nEnter a number between 1 and "+catalog.size()+":\n ");
    }
    return catalog.get(selection-1);
  }

  private int getAQuantity(int max) {
    Scanner scan = new Scanner(System.in);
    int quantity=0;
    while (quantity <= 0 || quantity >max)     {
      String line = scan.nextLine();
      quantity = Integer.parseInt(line);
      if (quantity <= 0 || quantity > max)
        System.out.println("\nEnter a number between 1 and "+max+":\n ");
    }
    return quantity;
  }

  private int GetSelectedCartItem(String prompt) {
    System.out.println("\nEnter the number of the product you want to "+prompt+":\n ");
    shoppingCart.displayCart();
    return  getAQuantity( shoppingCart.size());
  }

  private String GetSelection() {
    while (true) {
      System.out.println("\nMake a selection from this list (enter one letter and press Enter:");
      System.out.println("A = Add item to the shopping cart");

      if (shoppingCart.isEmpty()) {
        System.out.println("X = Exit");
      } else {
        System.out.println("T = Get current total");
        System.out.println("C = See contents of shopping cart");
        System.out.println("E = Edit quantity of items in shopping cart");
        System.out.println("R = Remove items from shopping cart");
        System.out.println("O = Check out");
      }

      Scanner scan = new Scanner(System.in);
      String line = scan.nextLine().toUpperCase();
      char c =  line.charAt(0);

      if (line.length() == 1 && "AXTCEOR".indexOf(c) != -1)
        return line;
    }
  }

  private void GetNameStateAndShipping()   {
    Scanner scan = new Scanner(System.in);

    System.out.println("\nWelcome to the Toy Yoda ordering system");
    System.out.println("Please enter your name to begin:\n");
    Name  = scan.nextLine();
    while (State.length() != 2) {
      System.out.println("Please enter your 2-letter state abbreviation:\n");
      State  = scan.nextLine();
    }
    State = State.toUpperCase();

    String shipping = "";
    while (!shipping.equals("S") && !shipping.equals("N")) {
      System.out.println("Please enter your shipping preference:\nS=Standard\nN=Next day:\n");
      shipping =  scan.nextLine();
      shipping = shipping.toUpperCase();
    }
    shippingType = (shipping.equals("S") ? ShippingType.STANDARD  : ShippingType.NEXT_DAY);
  }


  private void AddToCart()
  {
    Product product = GetSelectedProduct();
    System.out.println("Enter how many to add to cart (up to 100)");
    int quantity = getAQuantity(100);
    if (shoppingCart.AddToCart(product, quantity))
      System.out.println(product.name +" has been added to the shopping cart.");
  }

  private void DisplayTotals() {
    double cartPrice = shoppingCart.getTotalCartPrice();
    double tax = (State.equals("IL") || State.equals("NY") || State.equals("CA")) ? 0.06 * cartPrice : 0;
    double shippingPrice;
    if (shippingType.equals(ShippingType.STANDARD))
      shippingPrice = (cartPrice > 50) ? 0 : 10;
    else
      shippingPrice = 25;

    System.out.println("Total cost of items in cart: $" +  String.format("%,.2f",  cartPrice));
    System.out.println("Shipping: $" +  String.format("%,.2f",  shippingPrice));
    System.out.println("Tax: $" +  String.format("%,.2f",  tax));
    System.out.println("Grand total: $" +  String.format("%,.2f",  cartPrice+tax+shippingPrice));

  }

  private void EditQuantity() {
    int itemNumber =  GetSelectedCartItem("edit");
    System.out.println("Enter new quantity (up to 100)");
    int quantity = getAQuantity(100);
    LineItem item = shoppingCart.getLineItem(itemNumber-1);
    int oldQuantity = item.count;
    item.count = quantity;

    if (shoppingCart.getTotalCartPrice() < 99999.99) {
      System.out.println("Quantity updated.");
    }
    else {
      System.out.println("The total for the order cannot exceed $99,999.99.");
      item.count = oldQuantity;
    }
  }

  private void RemoveItem() {
    int itemNumber =  GetSelectedCartItem("remove");
    if (shoppingCart.removeItem(itemNumber-1))
      System.out.println("Item removed.");
  }

  public void HandleOrder() {
    populateCatalog();
    if (catalog.isEmpty()) return;

    GetNameStateAndShipping();

    System.out.println("\n\n");

    boolean done=false;
    while (!done) {
      String pick = GetSelection();
      switch (pick) {
        case "X":  // exit
          System.out.println("Goodbye and have a nice day. ");
          return;

        case "C":  // see contents of cart
          shoppingCart.displayCart();
          break;
        case "A":  // add to cart
          AddToCart();
          break;
        case "T": // get totals
          DisplayTotals();
          break;
        case "E": // edit quantity
          EditQuantity();
          break;
        case "R": // remove item
          RemoveItem();
          break;
        case "O":
          done=true;
          break;
      }
    }
    if (shoppingCart.isEmpty())
      System.out.println("You have nothing in your cart. The order is abandoned. See you later.");
    else {
      System.out.println("Thank you for the order, " + Name);
      System.out.println("Your order will be sent using " +
              (shippingType==ShippingType.STANDARD?"standard":"next day") + " shipping.\nYour total cost is:");
      DisplayTotals();
    }
  }

  private void populateCatalog(){
    File in_file = new File("catalog.csv");
    try {
      Scanner reader = new Scanner(in_file);

      while (reader.hasNextLine()) {
        String text_line = reader.nextLine();
        String[] parts = text_line.split(",");
        if (parts.length == 3)  {
          Product newProduct = new Product(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]));
          catalog.add(newProduct);
        }
      }
      reader.close();
    } catch (Exception e) {
      System.out.println("Error reading part catalog. Please re-install application.");
    }
  }
  public OrderingSystem()
  {
    catalog = new ArrayList<>();
    shoppingCart = new ShoppingCart();
  }
}
