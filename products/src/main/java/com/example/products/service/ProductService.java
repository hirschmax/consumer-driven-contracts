package com.example.products.service;

import com.example.products.model.ProductRequest;
import com.example.products.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import com.example.products.model.Product;

import java.util.List;

@ApplicationScoped
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getProducts(ProductRequest productRequest) {
        return repository.findProductsWhereIdIsIn(productRequest.ids());
    }
}