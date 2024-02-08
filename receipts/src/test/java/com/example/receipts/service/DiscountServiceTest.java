package com.example.receipts.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class DiscountServiceTest {

    @Inject
    DiscountService discountService;

    @Test
    void shouldBeAbleToAddDiscountCode() {
        discountService.addDiscountCode("TEST123", 12);
        assertThat(discountService.getDiscountPercent("TEST123")).isEqualTo(0.12);
    }

    @ParameterizedTest
    @CsvSource(value = {"20,100,80","10,99.89,89.90"})
    void shouldApplyDiscountCodeToCalculateCorrectDiscountedPrice(int discountPercent, double subTotal, double expectedTotalPrice) {
        discountService.addDiscountCode("TEST2024", discountPercent);
        double discountedPrice = discountService.applyDiscount(subTotal, "TEST2024").value();
        assertThat(discountedPrice).isEqualTo(expectedTotalPrice);
    }

}