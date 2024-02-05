package com.example.products.repository;

import com.example.products.model.Product;

import java.util.*;

public class MockProductRepository extends ProductRepository {

    private final Map<String, Product> productRepository = new HashMap<>();

    @Override
    public List<Product> findProductsWhereIdIsIn(List<String> ids) {
        return ids.stream().map(productRepository::get).filter(Objects::nonNull).toList();
    }

    public void setProductRepository(Map<String, Product> productRepository) {
        this.productRepository.putAll(productRepository);
    }

}
