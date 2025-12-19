package com.worker.orderworker.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "orders_processed")
@Data
public class ProcessedOrder {
    @Id
    private String id;
    private String orderId;
    private String customerId;
    private List<ProductDTO> products;
    private Double totalAmount;
}
