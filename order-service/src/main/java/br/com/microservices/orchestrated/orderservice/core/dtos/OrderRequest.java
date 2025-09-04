package br.com.microservices.orchestrated.orderservice.core.dtos;

import br.com.microservices.orchestrated.orderservice.core.models.OrderProducts;

import java.util.List;

public class OrderRequest {

    private List<OrderProducts> products;

    public OrderRequest() {
    }

    public OrderRequest(List<OrderProducts> products) {
        this.products = products;
    }

    public List<OrderProducts> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProducts> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "products=" + products +
                '}';
    }
}
