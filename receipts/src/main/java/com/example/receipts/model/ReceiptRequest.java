package com.example.receipts.model;

import java.util.List;

public record ReceiptRequest(List<Product> products, String discountCode) {
}
