package com.example.order.model;

import java.util.List;

public record OrderRequest(List<String> productIds, String discountCode) {
}
