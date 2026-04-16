import java.util.ArrayList;

public class ShoppingCart
{
  private final double maxCartValue = 99999.99;

  private ArrayList<LineItem> cartList;

  boolean AddToCart(Product product, int quantity) {
    double newTotal = getTotalCartPrice() + quantity * product.price;
    if (newTotal > maxCartValue) {
      System.out.println("Sorry, the total value of the cart can't exceed $"+
              String.format("%,.2f", maxCartValue)+".");
      return false;
    }

    // if this item is already in the cart then just increase the quantity
    for (LineItem item : cartList) {
      if (item.product.id == product.id)       {
        item.count += quantity;
        return true;
      }
    }
    LineItem newItem = new LineItem(product, quantity);
    cartList.add(newItem);
    return true;
  }

  void displayCart() {
    for (int i=0;i<cartList.size();i++) {
      LineItem item = cartList.get(i);
      System.out.println((i+1)+")  "+ item.product.name + " quantity: "+item.count+", total cost:"+String.format("%,.2f",  item.totalCost()));
    }
  }

  double getTotalCartPrice() {
    double sum = 0;
    for (LineItem item : cartList)
      sum+=item.totalCost();
    return sum;
  }

  boolean isEmpty() {
    return cartList.isEmpty();
  }

  int size() {
    return cartList.size();
  }
  public ShoppingCart()   {
    cartList = new ArrayList<>();
  }

  public LineItem getLineItem(int itemNo)   {
    return cartList.get(itemNo);
  }

  public boolean removeItem(int itemNo) {
    if (isEmpty() || itemNo >= cartList.size())
      return false;
    cartList.remove(itemNo);
    return true;
  }
}
