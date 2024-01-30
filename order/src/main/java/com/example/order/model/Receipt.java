package com.example.order.model;

import java.util.List;

public record Receipt(List<Product> products, Price total) {
}
