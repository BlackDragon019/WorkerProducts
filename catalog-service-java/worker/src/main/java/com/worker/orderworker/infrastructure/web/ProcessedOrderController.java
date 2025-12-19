package com.worker.orderworker.infrastructure.web;

import com.worker.orderworker.domain.model.ProcessedOrder;
import com.worker.orderworker.domain.repository.OrderMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal")
public class ProcessedOrderController {

    @Autowired
    private OrderMongoRepository repository;

    @GetMapping("/orders")
    public List<ProcessedOrder> getAll(){
        return repository.findAll();
    }
}
