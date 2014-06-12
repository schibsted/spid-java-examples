package no.spid.examples;

public class Product {
    final String description;
    final int price;
    final int vat;

    public Product(String description, int price, int vat) {
        this.description = description;
        this.price = price;
        this.vat = vat;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getVat() {
        return vat;
    }
}
