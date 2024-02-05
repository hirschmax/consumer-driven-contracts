package com.example.order;

import com.example.order.model.*;
import com.example.order.service.ReceiptService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;


@Path("/order")
public class UseCaseGetReceiptForOrder {

    private final ReceiptService receiptService;

    public UseCaseGetReceiptForOrder(@RestClient ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Receipt> calculateReceiptForOrder(OrderRequest orderRequest) {
        ReceiptRequest receiptRequest = new ReceiptRequest(orderRequest.productIds(), orderRequest.discountCode());
        Receipt receipt = receiptService.calculateReceipt(receiptRequest);
        return RestResponse.ResponseBuilder.ok(receipt).build();
    }
}
