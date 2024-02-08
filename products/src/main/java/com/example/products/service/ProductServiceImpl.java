package com.example.products.service;

import com.example.products.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import com.example.products.model.Product;

import java.util.List;

@ApplicationScoped
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> findProducts(List<String> productIds) {
        return repository.findProductsWhereIdIsIn(productIds);
    }

    @Override
    public void addProducts(List<Product> products) {
        repository.addProducts(products);
    }
}