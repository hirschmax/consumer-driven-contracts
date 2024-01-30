package com.example.order.model;

import java.util.List;

public record ReceiptRequest(List<Product> products, String discountCode) {
}
