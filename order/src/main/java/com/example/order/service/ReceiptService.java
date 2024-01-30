package com.example.order.service;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import com.example.order.model.Receipt;
import com.example.order.model.ReceiptRequest;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@Path("/receipts")
@RegisterRestClient(configKey = "receipts-api")
public interface ReceiptService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Receipt calculateReceipt(ReceiptRequest receiptRequest);

}
