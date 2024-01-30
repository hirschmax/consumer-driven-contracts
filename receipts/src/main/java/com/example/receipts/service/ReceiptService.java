package com.example.receipts.service;

import com.example.receipts.model.Price;
import com.example.receipts.model.Product;
import com.example.receipts.model.ReceiptResponse;
import com.example.receipts.model.ReceiptRequest;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ReceiptService {

    private static final Map<String, Integer> discountCodeToPercentMap = Map.of("FIVE", 5, "TEN", 10);
    public ReceiptResponse calculateReceipt(ReceiptRequest receiptRequest) {
        List<Product> products = receiptRequest.products();
        String discountCode = receiptRequest.discountCode();

        double subTotal = calculateSubTotal(products);
        int discountPercent = discountCodeToPercentMap.getOrDefault(discountCode, 0);

        double total = Math.round(subTotal * (100d - discountPercent)) / 100d;
        return new ReceiptResponse(products, new Price(total));
    }
    private double calculateSubTotal(List<Product> products) {
        return products.stream().mapToDouble(Product::price).sum();
    }
}
