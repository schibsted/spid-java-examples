package no.spid.examples;

public class OrderItem {
    final String name;
    final int price;
    final int vat;
    final int quantity;

    public OrderItem(Product product, int quantity) {
        this.name = product.getDescription();
        this.price = product.getPrice();
        this.vat = product.getVat();
        this.quantity = quantity;
    }

    // No need for getters, it's only used for JSON serialization
}
