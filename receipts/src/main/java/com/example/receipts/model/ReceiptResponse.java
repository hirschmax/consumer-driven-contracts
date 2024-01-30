package com.example.receipts.model;

import java.util.List;

public record ReceiptResponse(List<Product> products, Price total) {
}
