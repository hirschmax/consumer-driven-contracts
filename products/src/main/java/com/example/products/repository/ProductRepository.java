package com.example.products.repository;

import com.example.products.model.Product;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductRepository {

    private final Map<String, Product> productsMap = new HashMap<>();

    @PostConstruct
    void init() {
        productsMap.putAll(Map.of(
                "S1", new Product("S1", "Fries", 5.49),
                "S2", new Product("S2", "Soup", 7.99),
                "S3", new Product("S3", "Salad", 9.99)));
    }

    public List<Product> findProductsWhereIdIsIn(List<String> ids) {
        return ids.stream().map(productsMap::get).filter(Objects::nonNull).toList();
    }

    public void addProducts(List<Product> products) {
        productsMap.putAll(products.stream().collect(Collectors.toMap(Product::id, product -> product)));
    }
}
