package com.example.order.service;

import com.example.order.model.Price;
import com.example.order.model.Product;
import com.example.order.model.Receipt;
import com.example.order.model.ReceiptRequest;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MockReceiptService implements ReceiptService {


    private static final Map<String, Integer> discountCodeToPercentMap = Map.of("FIVE", 5, "TEN", 10);
    public Receipt calculateReceipt(ReceiptRequest receiptRequest) {
        List<Product> products = receiptRequest.products();
        String discountCode = receiptRequest.discountCode();

        double subTotal = calculateSubTotal(products);
        int discountPercent = discountCodeToPercentMap.getOrDefault(discountCode, 0);

        NumberFormat numberFormat = DecimalFormat.getInstance(Locale.ENGLISH);
        numberFormat.setMaximumIntegerDigits(2);
        String format = numberFormat.format(subTotal * (1d - discountPercent / 100d));
        double total = Double.parseDouble(format);

        return new Receipt(products, new Price(total));
    }
    private double calculateSubTotal(List<Product> products) {
        return products.stream().mapToDouble(Product::price).sum();
    }
}
