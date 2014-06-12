package no.spid.examples;

public class PaylinkItem {
    final String description;
    final int price;
    final int vat;
    final int quantity;

    public PaylinkItem(Product product, int quantity) {
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.vat = product.getVat();
        this.quantity = quantity;
    }

    // No need for getters, it's only used for JSON serialization
}
