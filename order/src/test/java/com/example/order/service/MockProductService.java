package com.example.order.service;

import com.example.order.model.Product;
import com.example.order.model.ProductsRequest;

import java.util.List;
import java.util.Map;

public class MockProductService implements ProductService {

    private static final Map<String, Product> productMap = Map.of(
            "M1", new Product("M1", "Fries", 5.49),
            "M2", new Product("M2", "Soup", 7.99),
            "M3", new Product("M3", "Salad", 9.99));

    @Override
    public List<Product> getProducts(ProductsRequest productRequest) {
        return productRequest.ids().stream().map(this::getProductForId).toList();
    }

    private Product getProductForId(String productId) {
        return productMap.get(productId);
    }
}
