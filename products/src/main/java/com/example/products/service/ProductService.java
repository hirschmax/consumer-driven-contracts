package com.example.products.service;

import com.example.products.exceptions.ProductNotFoundException;
import com.example.products.model.ProductRequest;
import jakarta.enterprise.context.ApplicationScoped;
import com.example.products.model.Product;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ProductService {

    private static final Map<String, Product> productMap = Map.of(
            "M1", new Product("M1", "Fries", 5.49),
            "M2", new Product("M2", "Soup", 7.99),
            "M3", new Product("M3", "Salad", 9.99));

    public List<Product> getProducts(ProductRequest productRequest) {
        return productRequest.ids().stream().map(this::getProductForId).toList();
    }

    private Product getProductForId(String productId) {
        Product product = productMap.get(productId);
        if (product != null) {
            return product;
        }
        throw new ProductNotFoundException(productId);
    }
}