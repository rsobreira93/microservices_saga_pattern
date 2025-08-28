package br.com.microservices.orchestrated.productvalidationservice.core.dtos;


public class OrderProducts {

    private Product product;

    private  int quantity;

    public OrderProducts() {
    }

    public OrderProducts(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
