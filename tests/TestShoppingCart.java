import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;


public class TestShoppingCart
{
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  // just add 1 item to the cart and make sure there is 1 item afterwards.
  @Test
  void TestAddOneItem() {
    Product product = new Product(42, "test", 45.50);
    ShoppingCart cart = new ShoppingCart();

    //make sure cart is empty
    assertTrue(cart.isEmpty());

    cart.AddToCart(product,1);

    assertEquals(cart.size(), 1);

    assertFalse(cart.isEmpty());
  }

  @Test
  void TestAddTwoItems() {

    Product product = new Product(42, "test", 45.50);
    Product product2 = new Product(44, "test2", 145.50);
    ShoppingCart cart = new ShoppingCart();

    //make sure cart is empty
    assertTrue(cart.isEmpty());

    cart.AddToCart(product,1);
    cart.AddToCart(product2,1);

    assertEquals(cart.size(), 2);

    assertFalse(cart.isEmpty());
  }

  //make sure if you add the same item again it
  @Test
  void TestAddSameItemTwice() {

    Product product = new Product(42, "test", 45.50);
    ShoppingCart cart = new ShoppingCart();

    //make sure cart is empty
    assertTrue(cart.isEmpty());

    cart.AddToCart(product,1);
    cart.AddToCart(product,1);

    assertEquals(1, cart.size());

    LineItem item = cart.getLineItem(0);

    // make sure it is  2x the cost
    assertEquals(91.0, item.totalCost());
  }

  @Test
  void TestAddTwoItemsAndRemoveOne() {

    Product product = new Product(42, "test", 45.50);
    Product product2 = new Product(44, "test2", 145.50);
    ShoppingCart cart = new ShoppingCart();

    //make sure cart is empty
    assertTrue(cart.isEmpty());

    cart.AddToCart(product,1);
    cart.AddToCart(product2,1);

    assertEquals(2, cart.size());

    cart.removeItem(1);

    assertFalse(cart.isEmpty());
    assertEquals(1, cart.size());

  }

  @Test
  void TestAddTwoItemsAndRemoveBoth() {

    Product product = new Product(42, "test", 45.50);
    Product product2 = new Product(44, "test2", 145.50);
    ShoppingCart cart = new ShoppingCart();

    //make sure cart is empty
    assertTrue(cart.isEmpty());

    cart.AddToCart(product,1);
    cart.AddToCart(product2,1);

    assertEquals(2, cart.size());

    cart.removeItem(1);
    cart.removeItem(0);

    assertTrue(cart.isEmpty());
  }

  @Test
  void TryToRemoveFromEmptyCart() {

    ShoppingCart cart = new ShoppingCart();

    //make sure cart is empty
    assertTrue(cart.isEmpty());

    boolean result = cart.removeItem(0);

    assertTrue(cart.isEmpty());
    assertFalse(result);
  }

  @Test
  void TestRemoveItemNotThere() {

    Product product = new Product(42, "test", 45.50);
    ShoppingCart cart = new ShoppingCart();
    cart.AddToCart(product,1);

    assertEquals(1, cart.size());

    boolean result = cart.removeItem(1);

    assertFalse(result);
    assertFalse(cart.isEmpty());
  }

  @Test
  void TestDisplayCart() {

    Product product = new Product(42, "test", 45.50);
    Product product2 = new Product(44, "test2", 145.50);
    ShoppingCart cart = new ShoppingCart();

    //make sure cart is empty
    assertTrue(cart.isEmpty());

    cart.AddToCart(product,1);
    cart.AddToCart(product2,1);

    System.setOut(new PrintStream(outputStream));

    // sends output to outputStream
    cart.displayCart();
    System.setOut(originalOut);

    String result = outputStream.toString().trim();

    assertEquals("1)  test quantity: 1, total cost:45.50\r\n2)  test2 quantity: 1, total cost:145.50", result);
  }

  @Test
  void ExceedCartLimit() {

    Product product = new Product(42, "test", 99999.50);
    Product product2 = new Product(44, "test2", 0.50);
    ShoppingCart cart = new ShoppingCart();

    boolean result1 = cart.AddToCart(product,1);
    boolean result2 = cart.AddToCart(product2,1);

    assertEquals(cart.size(), 1);

    assertTrue(result1);
    assertFalse(result2);
  }


}
