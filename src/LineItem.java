public class LineItem
{
  public Product product;
  public int count;
  public double totalCost() {
    return count * product.price;
  }

  LineItem(Product product, int count)   {
    this.product = product;
    this.count = count;
  }
}
