package com.example.products.service;


import com.example.products.model.Product;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class ProductServiceTest {

    @Inject
    ProductService productService;

    @BeforeEach
    void setUp() {
        productService.addProducts(List.of(
                new Product("A1", "Product A", 9.99),
                new Product("A2", "Product B", 9.99),
                new Product("A3", "Product C", 9.99)
        ));
    }

    @Test
    void shouldFindAllProducts() {
        List<Product> products = productService.findProducts(List.of("A1", "A2", "A3"));
        assertThat(products).hasSize(3);
        assertThat(products.stream().map(Product::name)).contains("Product A", "Product B", "Product C");
    }

    @Test
    void shouldFindTwoProducts() {
        List<Product> products = productService.findProducts(List.of("A1", "A2", "M3"));
        assertThat(products).hasSize(2);
        assertThat(products.stream().map(Product::name)).contains("Product A", "Product B");
    }

}