package com.example.receipts.service;

import com.example.receipts.model.Product;
import com.example.receipts.model.ProductsRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockProductService implements ProductService {

    private final Map<String, Product> productRepository = new HashMap<>();

    @Override
    public List<Product> getProducts(ProductsRequest productRequest) {
        return productRequest.ids().stream().map(this::getProductForId).toList();
    }

    public void setProductRepository(Map<String, Product> productRepository) {
        this.productRepository.putAll(productRepository);
    }

    private Product getProductForId(String productId) {
        return productRepository.get(productId);
    }
}