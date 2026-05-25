import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLineItem
{
  @Test
  void TestMultipleItemsOfAProduct() {
    Product product = new Product(42, "test", 45.50);
    LineItem item = new LineItem(product, 4);

    double expected = 182.0;
    double actual = item.totalCost();

    assertEquals(expected, actual);

  }
}
