package com.example.products.repository;

import com.example.products.model.Product;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class ProductRepository {

    private static final Map<String, Product> productsMap = Map.of(
            "S1", new Product("S1", "Fries", 5.49),
            "S2", new Product("S2", "Soup", 7.99),
            "S3", new Product("S3", "Salad", 9.99));

    public List<Product> findProductsWhereIdIsIn(List<String> ids) {
        return ids.stream().map(productsMap::get).filter(Objects::nonNull).toList();
    }


}
