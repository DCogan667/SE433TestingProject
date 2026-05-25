public class Messages
{
  public final static String welcomeMessage = "Welcome to the Toy Yoda ordering system ";
  public final static String namePrompt = "Please enter your name to begin: ";
  public final static String stateMessage = "Please enter your 2-letter state abbreviation: ";

  public final static String abandonedMessage = "You have nothing in your cart. The order is abandoned. See you later. ";

  public final static String orderTooLargeMessage = "The total for the order cannot exceed $99,999.99. ";
  public final static String cartTooLargeMessage = "Sorry, the total value of the cart can't exceed $99,999.99. ";

  public final static String shipMenu = """
          Please enter your shipping preference:
          S=Standard
          N=Next day:
          """;

  public final static String menu1 = """
          Make a selection from this list (enter one letter and press Enter:
          A = Add item to the shopping cart
          X = Exit
          """;

  public final static String menu2 = """
          Make a selection from this list (enter one letter and press Enter:
          A = Add item to the shopping cart
          T = Get current total
          C = See contents of shopping cart
          E = Edit quantity of items in shopping cart
          R = Remove items from shopping cart
          O = Check out
          """;


  public final static String newQuantityPrompt = "Enter new quantity (up to 100) ";

  public final static String quantityUpdatedMessage = "Quantity updated. ";

  public final static String quantityToAddPrompt = "Enter how many to add to cart (up to 100) ";

  public final static String editNumberPrompt = "Enter the number of the product you want to edit: ";

  public final static String removeNumberPrompt = "Enter the number of the product you want to remove: ";

  public final static String goodbyeMessage = "Goodbye and have a nice day. ";

  public final static String catalogReadErrorMessage = "Error reading part catalog. Please re-install application. ";

  public final static String removedMessage = "Item removed. ";

  public static String cleanString(String inString) {
    return inString.trim().replaceAll("\\s+", " ");
  }

}
