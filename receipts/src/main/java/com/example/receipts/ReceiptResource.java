package com.example.receipts;

import com.example.receipts.model.ReceiptResponse;
import com.example.receipts.model.ReceiptRequest;
import com.example.receipts.service.ReceiptService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
public class ReceiptResource {

    @Inject
    ReceiptService receiptService;

    @POST
    @Path("/receipts")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response calculateReceipt(ReceiptRequest receiptRequest) {
        ReceiptResponse receiptResponse = receiptService.calculateReceipt(receiptRequest);
        return Response.ok(receiptResponse).build();
    }

}
