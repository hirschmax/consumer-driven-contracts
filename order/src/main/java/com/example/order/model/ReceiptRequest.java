package com.example.order.model;

import java.util.List;

public record ReceiptRequest(List<String> productIds, String discountCode) {
}
