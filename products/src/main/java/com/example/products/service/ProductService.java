package com.example.products.service;

import com.example.products.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> findProducts(List<String> productIds);
    void addProducts(List<Product> products);
}
