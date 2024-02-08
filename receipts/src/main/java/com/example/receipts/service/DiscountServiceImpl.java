package com.example.receipts.service;

import com.example.receipts.model.Price;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class DiscountServiceImpl implements DiscountService {
    private final NumberFormat numberFormat = DecimalFormat.getInstance(Locale.ENGLISH);
    private final Map<String, Integer> discountRepository = new HashMap<>();

    @PostConstruct
    void init() {
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setGroupingUsed(false);
        discountRepository.putAll(Map.of("FIVE", 5, "TEN", 10));
    }

    @Override
    public Price applyDiscount(double subTotal, String discountCode) {
        double discountPercent = getDiscountPercent(discountCode);
        double discount = subTotal * discountPercent;
        return new Price(formatToMaximumTwoFractionDigits(subTotal, discount));
    }

    @Override
    public double getDiscountPercent(String discountCode) {
        return discountRepository.getOrDefault(discountCode, 0) * 0.01;
    }

    @Override
    public void addDiscountCode(String discountCode, int discountPercent) {
        discountRepository.put(discountCode, discountPercent);
    }

    private double formatToMaximumTwoFractionDigits(double subTotal, double discount) {
        return Double.parseDouble(numberFormat.format(subTotal - discount));
    }

}
