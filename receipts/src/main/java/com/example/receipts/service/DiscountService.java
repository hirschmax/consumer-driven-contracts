package com.example.receipts.service;

import com.example.receipts.model.Price;

public interface DiscountService {
    Price applyDiscount(double subTotal, String discountCode);
    double getDiscountPercent(String discountCode);
    void addDiscountCode(String discountCode, int discountPercent);
}
