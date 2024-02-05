package com.example.order.service;

import com.example.order.model.Price;
import com.example.order.model.Product;
import com.example.order.model.Receipt;
import com.example.order.model.ReceiptRequest;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class MockReceiptService implements ReceiptService {

    private final Map<String, Integer> discountRepository = new HashMap<>();
    private final Map<String, Product> productRepository = new HashMap<>();

    public void setProductRepository(Map<String, Product> productRepository) {
        this.productRepository.putAll(productRepository);
    }
    public void setDiscountRepository(Map<String, Integer> discountRepository) {
        this.discountRepository.putAll(discountRepository);
    }

    @Override
    public Receipt calculateReceipt(ReceiptRequest receiptRequest) {
        List<Product> products = getProducts(receiptRequest);
        String discountCode = receiptRequest.discountCode();

        double subTotal = calculateSubTotal(products);
        double discount = calculateDiscount(discountCode, subTotal);

        NumberFormat numberFormat = DecimalFormat.getInstance(Locale.ENGLISH);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setGroupingUsed(false);
        String format = numberFormat.format(subTotal - discount);

        return new Receipt(products, new Price(Double.parseDouble(format)));
    }

    private double calculateDiscount(String discountCode, double subTotal) {
        double discountPercent = discountRepository.getOrDefault(discountCode, 0) * 0.01;
        return subTotal * discountPercent;
    }

    private List<Product> getProducts(ReceiptRequest receiptRequest) {
        return receiptRequest.productIds().stream().map(productRepository::get).filter(Objects::nonNull).toList();
    }

    private double calculateSubTotal(List<Product> products) {
        return products.stream().mapToDouble(Product::price).sum();
    }
}
