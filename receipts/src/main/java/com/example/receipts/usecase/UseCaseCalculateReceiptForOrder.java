package com.example.receipts.usecase;

import com.example.receipts.model.ReceiptResponse;
import com.example.receipts.model.ReceiptRequest;
import com.example.receipts.service.ReceiptService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/api")
public class UseCaseCalculateReceiptForOrder {

    @Inject
    ReceiptService receiptService;

    @POST
    @Path("/receipts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<ReceiptResponse> calculateReceipt(ReceiptRequest receiptRequest) {
        ReceiptResponse receipt = receiptService.calculateReceipt(receiptRequest);
        return RestResponse.ResponseBuilder.ok(receipt).build();
    }

}
