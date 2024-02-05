package com.example.receipts.service;

import com.example.receipts.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@ApplicationScoped
public class ReceiptService {


    private final Map<String, Integer> discountRepository = Map.of("FIVE", 5, "TEN", 10);

    private final ProductService productService;

    public ReceiptService(@RestClient ProductService productService) {
        this.productService = productService;
    }

    public ReceiptResponse calculateReceipt(ReceiptRequest receiptRequest) {
        List<Product> products = getProducts(receiptRequest);
        String discountCode = receiptRequest.discountCode();

        double subTotal = calculateSubTotal(products);
        double discount = calculateDiscount(discountCode, subTotal);

        NumberFormat numberFormat = DecimalFormat.getInstance(Locale.ENGLISH);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setGroupingUsed(false);
        String format = numberFormat.format(subTotal - discount);

        return new ReceiptResponse(products, new Price(Double.parseDouble(format)));
    }

    private double calculateDiscount(String discountCode, double subTotal) {
        double discountPercent = discountRepository.getOrDefault(discountCode, 0) * 0.01;
        return subTotal * discountPercent;
    }

    private List<Product> getProducts(ReceiptRequest receiptRequest) {
        return this.productService.getProducts(new ProductsRequest(receiptRequest.productIds()));
    }

    private double calculateSubTotal(List<Product> products) {
        return products.stream().mapToDouble(Product::price).sum();
    }
}
