package com.example.receipts.service;

import com.example.receipts.model.ReceiptRequest;
import com.example.receipts.model.ReceiptResponse;

public interface ReceiptService {
    ReceiptResponse calculateReceipt(ReceiptRequest receiptRequest);
}
