package com.example.receipts.service;

import com.example.receipts.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.*;

@ApplicationScoped
public class ReceiptServiceImpl implements ReceiptService {

    private final ProductService productService;
    private final DiscountService discountService;

    public ReceiptServiceImpl(@RestClient ProductService productService, DiscountService discountService) {
        this.productService = productService;
        this.discountService = discountService;
    }

    @Override
    public ReceiptResponse calculateReceipt(ReceiptRequest receiptRequest) {
        List<Product> products = getProducts(receiptRequest);
        double subTotal = calculateSubTotal(products);
        Price price = discountService.applyDiscount(subTotal, receiptRequest.discountCode());
        return new ReceiptResponse(products, price);
    }

    private List<Product> getProducts(ReceiptRequest receiptRequest) {
        return this.productService.getProducts(new ProductsRequest(receiptRequest.productIds()));
    }

    private double calculateSubTotal(List<Product> products) {
        return products.stream().mapToDouble(Product::price).sum();
    }
}
