import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestOrderingSystem
{
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  
  private final String catalogText = "Enter the number of the product you want to order: 1) Air Filter 11.95 " +
          "2) SAE 0W20 Oil 15.50 3) Spark Plugs 32.65 4) Seat Covers 295.50 5) Floor Mats 99.99 " +
          "6) Sunshade 35.80 7) Dash cam 493.88 8) Phone Mount 25.75 9) Head Rest Cover 45.83 " +
          "10) Bluetooth Adapter 85.90 11) Emergency Kit 95.11 12) Radial Tire 85.60 " +
          "13) Brake Pads and Calipers 455.65 14) 12-volt Battery 85.50 " +
          "15) Toy Yoda Camry 24,995.00 16) Toy Yoda Prius Prime 42,850.00 ";

  private final String testCatalogText = "Enter the number of the product you want to order:  1) Product A 99,999.98 " +
          "2) Product B 99,999.99 3) One Penny Product 0.01 4) Fifty Bucks 49.99 5) Product C 99,999.97 ";

  private final static String addedPadsMessage = "Brake Pads and Calipers has been added to the shopping cart.\n";

  private final static String startupString = Messages.welcomeMessage + Messages.namePrompt + Messages.stateMessage
          + Messages.shipMenu  + Messages.menu1;

  String RunTestOrder(String simulatedInput, String expected, String database) {
    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
    System.setOut(new PrintStream(outputStream));

    OrderingSystem orderingSystem = new OrderingSystem();

    // sends output to outputStream
    orderingSystem.HandleOrder(database);
    System.setOut(originalOut);

    String result = Messages.cleanString( outputStream.toString());

    String cleanExpected = Messages.cleanString(expected);
    assertEquals(cleanExpected, result);

    return result;
  }

  String RunTestOrder(String simulatedInput, String expected) {
    return RunTestOrder(simulatedInput, expected, "catalog.csv");
  }


    @Test
  void testJustExit()
  {
    // The string input below is Name=Joe, State is NJ, S=Standard shipping
    // Then X to exit.
    String simulatedInput = "Joe\nNJ\nS\nX\n";

    String expected = startupString + Messages.goodbyeMessage;

    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void testMain()
  {
    // The string input below is Name=Joe, State is NJ, S=Standard shipping
    // Then X to exit.
    String simulatedInput = "Joe\nNJ\nS\nX\n";

    String expected = startupString + Messages.goodbyeMessage;

    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
    System.setOut(new PrintStream(outputStream));

    Main.main(new String[0]);
    System.setOut(originalOut);

    String result = Messages.cleanString( outputStream.toString());

    String cleanExpected = Messages.cleanString(expected);
    assertEquals(cleanExpected, result);
  }

  @Test
  void badChoiceThenExit()
  {
    // The command Y below is bogus
    String simulatedInput = "David\nTX\nS\nY\nX\n";

    String expected = startupString + Messages.menu1 + Messages.goodbyeMessage;

    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void tooLongChoiceThenExit()
  {
    // The command YYY below is bogus (too long)
    String simulatedInput = "David\nTX\nS\nYYY\nX\n";

    String expected = startupString + Messages.menu1 + Messages.goodbyeMessage;

    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void databaseNotFound()
  {
    // The command Y below is bogus
    String simulatedInput = "";

    String expected = Messages.catalogReadErrorMessage;

    RunTestOrder(simulatedInput, expected, "NoDatabase.csv");
  }

  @Test
  void checkOutWithNoCart()
  {
    // The string input below is Name=Joe, State is NJ, S=Standard shipping
    // Then X to exit.
    String simulatedInput = "Joe\nNJ\nS\nO\n";

    String expected = startupString + Messages.abandonedMessage;

    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void testIncorrectShippingThenExit()
  {
    // W = invalid shipping option
    String simulatedInput = "Joe\nNJ\nW\nS\nX\n";

    String expected = Messages.welcomeMessage + Messages.namePrompt + Messages.stateMessage
            + Messages.shipMenu + Messages.shipMenu + Messages.menu1  + Messages.goodbyeMessage;

    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void Order10OfAnItemViewCartAndCheckOut()
  {
    // This is NJ, no tax, no shipping because it's over the limit
    String simulatedInput = "Jim\nNJ\nS\nA\n13\n100\nC\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 100, total cost:45,565.00 ";
    String lastMessage = "Thank you for the order, Jim Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $45,565.00 Shipping: $0.00 " +
            "Tax: $0.00 Grand total: $45,565.00 Transaction completed";

    String expected = startupString + catalogText  + Messages.quantityToAddPrompt + addedPadsMessage
            + Messages.menu2 + cartContents + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void OrderTooMuchOfAnItem()
  {
    // Try to order 100 cars = too expensive
    String simulatedInput = "Jim\nNJ\nS\nA\n16\n100\nX\n";

    String expected = startupString + catalogText  + Messages.quantityToAddPrompt
            + Messages.cartTooLargeMessage + Messages.menu1 + Messages.goodbyeMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void OrderTooMuchOfAnItemWithinAPenny()
  {
    // Use the test catalog to try to add something for 99,999.98 and add 2 things that are worth a penny
    // use a special test catalog so it's not what the user sees.
    String simulatedInput = "Jim\nNJ\nS\nA\n1\n1\nA\n3\n1\nA\n3\n1\nX\n";

    String addedMessage1 = "Product A has been added to the shopping cart. ";
    String addedMessage2 = "One Penny Product has been added to the shopping cart. ";

    String expected = startupString + testCatalogText  + Messages.quantityToAddPrompt + addedMessage1
            + Messages.menu2 + testCatalogText  + Messages.quantityToAddPrompt + addedMessage2
            + Messages.menu2 + testCatalogText  + Messages.quantityToAddPrompt
            + Messages.cartTooLargeMessage + Messages.menu2 + Messages.goodbyeMessage;

    RunTestOrder(simulatedInput, expected, "TestCatalog.csv");
  }

  @Test
  void ChangeQtyTooMuchWithinAPenny()
  {
    // Use the test catalog to try to add something for 99,999.97 and add 1 thing worth a penny
    // and try to change it to 2 then to 3
    String simulatedInput = "Jim\nNJ\nS\nA\n5\n1\nA\n3\n1\nE\n2\n2\nE\n2\n3\nX\n";

    String addedMessage1 = "Product C has been added to the shopping cart. ";
    String addedMessage2 = "One Penny Product has been added to the shopping cart. ";

    String testCart = "1) Product C quantity: 1, total cost:99,999.97 2) One Penny Product quantity: 1, total cost:0.01 ";
    String testCart2 = "1) Product C quantity: 1, total cost:99,999.97 2) One Penny Product quantity: 2, total cost:0.02 ";

    String expected = startupString + testCatalogText  + Messages.quantityToAddPrompt + addedMessage1
            + Messages.menu2 + testCatalogText  + Messages.quantityToAddPrompt + addedMessage2
            + Messages.menu2 + Messages.editNumberPrompt + testCart  + Messages.newQuantityPrompt + Messages.quantityUpdatedMessage
            + Messages.menu2 + Messages.editNumberPrompt + testCart2  + Messages.newQuantityPrompt + Messages.orderTooLargeMessage
            + Messages.menu2 + Messages.goodbyeMessage;

    RunTestOrder(simulatedInput, expected, "TestCatalog.csv");
  }

  @Test
  void OrderTooMuchOfAnItemWithinAPenny2()
  {
    // Use the test catalog to try to add something for 99,999.98 and add 2 things that are worth a penny
    // use a special test catalog so it's not what the user sees.
    String simulatedInput = "Jim\nNJ\nS\nA\n2\n1\nA\n3\n1\nA\n3\n1\nX\n";

    String addedMessage1 = "Product B has been added to the shopping cart. ";
    String addedMessage2 = "One Penny Product has been added to the shopping cart. ";

    String expected = startupString + testCatalogText  + Messages.quantityToAddPrompt + addedMessage1
            + Messages.menu2 + testCatalogText  + Messages.quantityToAddPrompt + Messages.cartTooLargeMessage
            + Messages.menu2 + testCatalogText  + Messages.quantityToAddPrompt + Messages.cartTooLargeMessage
            + Messages.menu2 + Messages.goodbyeMessage;

    RunTestOrder(simulatedInput, expected, "TestCatalog.csv");
  }


  @Test
  void Order10OfAnItemViewCartAndCheckOutTaxableIL()
  {
    // This is IL, so add tax, no shipping because it's over the limit
    String simulatedInput = "Sally\nIL\nS\nA\n13\n100\nC\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 100, total cost:45,565.00 ";
    String lastMessage = "Thank you for the order, Sally Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $45,565.00 Shipping: $0.00 " +
            "Tax: $2,733.90 Grand total: $48,298.90 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedPadsMessage
            +Messages.menu2 + cartContents + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void Order10OfAnItemViewCartAndCheckOutTaxableILExpressShipping()
  {
    // This is IL, so add tax, no shipping because it's over the limit
    String simulatedInput = "Sally\nIL\nN\nA\n13\n100\nC\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 100, total cost:45,565.00 ";
    String lastMessage = "Thank you for the order, Sally Your order will be sent using next day shipping. " +
            "Your total cost is: Total cost of items in cart: $45,565.00 Shipping: $25.00 " +
            "Tax: $2,733.90 Grand total: $48,323.90 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedPadsMessage
            +Messages.menu2 + cartContents + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void Order1OfAnItemIncorrectChoiceAndCheckOut()
  {
    // This is IL, so add tax, no shipping because it's over the limit
    //   The 22 and -1 below are invalid choices and 0 and 17 for borders
    String simulatedInput = "Sally\nIL\nN\nA\n22\n-1\n0\n17\nSix\n13\n1\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 1, total cost:455.65 ";
    String lastMessage = "Thank you for the order, Sally Your order will be sent using next day shipping. " +
            "Your total cost is: Total cost of items in cart: $455.65 Shipping: $25.00 " +
            "Tax: $27.34 Grand total: $507.99 Transaction completed";

    String choiceError = "Enter a number between 1 and 16: ";

    String expected = startupString + catalogText + choiceError + choiceError + choiceError + choiceError + choiceError
            + Messages.quantityToAddPrompt + addedPadsMessage
            +Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void Order10OfAnItemViewCartAndCheckOutTaxableNY()
  {
    // This is NJ, no tax, no shipping because it's over the limit
    String simulatedInput = "Sally\nNY\nS\nA\n13\n100\nC\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 100, total cost:45,565.00 ";
    String lastMessage = "Thank you for the order, Sally Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $45,565.00 Shipping: $0.00 " +
            "Tax: $2,733.90 Grand total: $48,298.90 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedPadsMessage
            +Messages.menu2 + cartContents + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void Order10OfAnItemViewCartAndCheckOutTaxableCA()
  {
    // This is NJ, no tax, no shipping because it's over the limit
    String simulatedInput = "Sally\nCA\nS\nA\n13\n100\nC\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 100, total cost:45,565.00 ";
    String lastMessage = "Thank you for the order, Sally Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $45,565.00 Shipping: $0.00 " +
            "Tax: $2,733.90 Grand total: $48,298.90 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedPadsMessage
            +Messages.menu2 + cartContents + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void Order10OfAnItemViewCartAndRemove1AndCheckOut()
  {
    // This is NJ, no tax, no shipping because it's over the limit
    String simulatedInput = "Paul\nNJ\nS\nA\n13\n100\nE\n1\n99\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 100, total cost:45,565.00 ";

    String lastMessage = "Thank you for the order, Paul Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $45,109.35 Shipping: $0.00 " +
            "Tax: $0.00 Grand total: $45,109.35 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedPadsMessage
            +Messages.menu2 + Messages.editNumberPrompt + cartContents + Messages.newQuantityPrompt
            + Messages.quantityUpdatedMessage + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void Order2ItemsRemove1AndCheckOut()
  {
    // This is NJ, no tax, standard shipping
    //  The codes below A 13 = Add item 13, A 1 = Add item 1 Then R 1 = remove item 1
    String simulatedInput = "Paul\nNJ\nS\nA\n13\n1\nA\n1\n1\nR\n1\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 1, total cost:455.65 ";
    String addedFilterMessage = "Air Filter has been added to the shopping cart. ";
    String shoppingCart2 = "1) Brake Pads and Calipers quantity: 1, total cost:455.65 2) Air Filter quantity: 1, total cost:11.95 ";

    String lastMessage = "Thank you for the order, Paul Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $11.95 Shipping: $10.00 " +
            "Tax: $0.00 Grand total: $21.95 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedPadsMessage
            + Messages.menu2 + catalogText + Messages.quantityToAddPrompt + addedFilterMessage
            + Messages.menu2 + Messages.removeNumberPrompt  + shoppingCart2 + Messages.removedMessage
            + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void Order2ItemsFailToRemove1AndCheckOut()
  {
    // This is NJ, no tax, standard shipping
    // the 4 below is item #4 we try to remove. That should fail.
    // Then try again with the 1 option
    String simulatedInput = "Paul\nNJ\nS\nA\n13\n1\nA\n1\n1\nR\n0\n-1\nSix\n3\n1\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 1, total cost:455.65 ";
    String addedFilterMessage = "Air Filter has been added to the shopping cart. ";
    String shoppingCart2 = "1) Brake Pads and Calipers quantity: 1, total cost:455.65 2) Air Filter quantity: 1, total cost:11.95 ";

    String removalError = "Enter a number between 1 and 2: ";
    String lastMessage = "Thank you for the order, Paul Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $11.95 Shipping: $10.00 " +
            "Tax: $0.00 Grand total: $21.95 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedPadsMessage
            + Messages.menu2 + catalogText + Messages.quantityToAddPrompt + addedFilterMessage
            + Messages.menu2 + Messages.removeNumberPrompt + shoppingCart2 + removalError + removalError
            + removalError + removalError + Messages.removedMessage
            + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void Order2ItemsFailToRemoveTooLowAndCheckOut()
  {
    // This is NJ, no tax, standard shipping
    // the 4 below is item #-1 we try to remove. That should fail.
    // Then try again with the 1 option
    String simulatedInput = "Paul\nNJ\nS\nA\n13\n1\nA\n1\n1\nR\n-1\n1\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 1, total cost:455.65 ";
    String addedFilterMessage = "Air Filter has been added to the shopping cart. ";
    String shoppingCart2 = "1) Brake Pads and Calipers quantity: 1, total cost:455.65 2) Air Filter quantity: 1, total cost:11.95 ";

    String removalError = "Enter a number between 1 and 2: ";
    String lastMessage = "Thank you for the order, Paul Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $11.95 Shipping: $10.00 " +
            "Tax: $0.00 Grand total: $21.95 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedPadsMessage
            + Messages.menu2 + catalogText + Messages.quantityToAddPrompt + addedFilterMessage
            + Messages.menu2 + Messages.removeNumberPrompt + shoppingCart2 + removalError + Messages.removedMessage
            + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void Order2ItemsGetTotalAndCheckOut()
  {
    // New in this test is the get total function
    String simulatedInput = "Paul\nNJ\nS\nA\n13\n1\nA\n1\n1\nT\nO\n";

    String addedFilterMessage = "Air Filter has been added to the shopping cart. ";

    String totalMessage = "Total cost of items in cart: $467.60 Shipping: $0.00 Tax: $0.00 Grand total: $467.60 ";

    String lastMessage = "Thank you for the order, Paul Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $467.60 Shipping: $0.00 " +
            "Tax: $0.00 Grand total: $467.60 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedPadsMessage
            + Messages.menu2 + catalogText + Messages.quantityToAddPrompt + addedFilterMessage
            + Messages.menu2 + totalMessage + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void CheckTotalsNear50Dollars()
  {
    // New in this test is the get total function near $50. Test at 49.99, 50.00 and 50.01.
    // Only 50.01 should be free shipping
    String simulatedInput = "Paul\nNJ\nS\nA\n4\n1\nT\nA\n3\n1\nT\nA\n3\n1\nT\nO\n";

    String addedMessage1 = "Fifty Bucks has been added to the shopping cart. ";
    String addedMessage2 = "One Penny Product has been added to the shopping cart. ";

    String totalMessage1 = "Total cost of items in cart: $49.99 Shipping: $10.00 Tax: $0.00 Grand total: $59.99 ";
    String totalMessage2 = "Total cost of items in cart: $50.00 Shipping: $10.00 Tax: $0.00 Grand total: $60.00 ";
    String totalMessage3 = "Total cost of items in cart: $50.01 Shipping: $0.00 Tax: $0.00 Grand total: $50.01 ";

    String lastMessage = "Thank you for the order, Paul Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $50.01 Shipping: $0.00 " +
            "Tax: $0.00 Grand total: $50.01 Transaction completed";

    String expected = startupString + testCatalogText + Messages.quantityToAddPrompt + addedMessage1
            + Messages.menu2 + totalMessage1
            + Messages.menu2 + testCatalogText + Messages.quantityToAddPrompt + addedMessage2
            + Messages.menu2 + totalMessage2
            + Messages.menu2 + testCatalogText + Messages.quantityToAddPrompt + addedMessage2
            + Messages.menu2 + totalMessage3
            + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected, "TestCatalog.csv");
  }

  @Test
  void Order10OfAnItemViewCarAndEditQuantityWithError()
  {
    // This is NJ, no tax, no shipping because it's over the limit
    // This will try to change the quantity but it will try to change product #2 when there is just one
    String simulatedInput = "Paul\nNJ\nS\nA\n13\n100\nE\n2\n1\nSix\n99\nO\n";

    String cartContents = "1) Brake Pads and Calipers quantity: 100, total cost:45,565.00 ";
    String QuantityError = "Enter a number between 1 and 100: ";

    String quantityErrorMessage = "Enter a number between 1 and 1: ";
    String lastMessage = "Thank you for the order, Paul Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $45,109.35 Shipping: $0.00 " +
            "Tax: $0.00 Grand total: $45,109.35 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedPadsMessage
            + Messages.menu2 + Messages.editNumberPrompt + cartContents + quantityErrorMessage
            + Messages.newQuantityPrompt + QuantityError + Messages.quantityUpdatedMessage
            + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void TryToExceedLimitByEditingQuantity()
  {
    // This is NJ, no tax, no shipping because it's over the limit
    String simulatedInput = "Paul\nNJ\nS\nA\n15\n1\nE\n1\n50\nO\n";

    String cartContents = "1) Toy Yoda Camry quantity: 1, total cost:24,995.00 ";
    String addedMessage = "Toy Yoda Camry has been added to the shopping cart. ";

    String lastMessage = "Thank you for the order, Paul Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $24,995.00 Shipping: $0.00 " +
            "Tax: $0.00 Grand total: $24,995.00\nTransaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedMessage
            + Messages.menu2 + Messages.editNumberPrompt + cartContents  + Messages.newQuantityPrompt  + Messages.orderTooLargeMessage
            + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }

  @Test
  void TryToExceedLimitByEditingQuantityCloseProximity()
  {
    // This is NJ, no tax, no shipping because it's over the limit
    String simulatedInput = "Paul\nNJ\nS\nA\n15\n4\nA\n1\n1\nE\n2\n2\nO\n";

    String cartContents1 = "1) Toy Yoda Camry quantity: 4, total cost:99,980.00 2) Air Filter quantity: 1, total cost:11.95 ";
    String addedMessage1 = "Toy Yoda Camry has been added to the shopping cart. ";
    String addedMessage2 = "Air Filter has been added to the shopping cart. ";

    String lastMessage = "Thank you for the order, Paul Your order will be sent using standard shipping. " +
            "Your total cost is: Total cost of items in cart: $99,991.95 Shipping: $0.00 " +
            "Tax: $0.00 Grand total: $99,991.95 Transaction completed";

    String expected = startupString + catalogText + Messages.quantityToAddPrompt + addedMessage1
            + Messages.menu2 + catalogText + Messages.quantityToAddPrompt + addedMessage2
            + Messages.menu2 + Messages.editNumberPrompt + cartContents1   + Messages.newQuantityPrompt
            + Messages.orderTooLargeMessage
            + Messages.menu2 + lastMessage;
    RunTestOrder(simulatedInput, expected);
  }


}


