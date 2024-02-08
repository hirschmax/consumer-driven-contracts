package com.example.receipts.service;

import com.example.receipts.model.Product;
import com.example.receipts.model.ReceiptRequest;
import com.example.receipts.model.ReceiptResponse;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class ReceiptServiceTest {

    @Inject
    ReceiptService receiptService;
    @Inject
    DiscountService discountService;

    @BeforeEach
    void setUp() {
        MockProductService productServiceMock = new MockProductService();
        productServiceMock.setProductRepository(Map.of(
                "A1", new Product("A1", "Product A", 5.99),
                "A2", new Product("A2", "Product B", 8.49),
                "A3", new Product("A3", "Product C", 9.99)
        ));
        QuarkusMock.installMockForType(productServiceMock, ProductService.class, RestClient.LITERAL);
        discountService.addDiscountCode("TEST2024", 20);
    }

    @Test
    void shouldCalculateReceiptWithDiscountedPrice() {
        ReceiptResponse receipt = receiptService.calculateReceipt(new ReceiptRequest(List.of("A1", "A2", "A3"), "TEST2024"));
        assertThat(receipt.total().value()).isEqualTo(19.58);
    }
}