package com.worker.orderworker.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderMessage {
    private String orderId;
    private String customerId;
    private List<String> productIds; //Lista de ids para buscar en go
}
