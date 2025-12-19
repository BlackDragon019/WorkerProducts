package com.worker.orderworker.domain.repository;

import com.worker.orderworker.domain.model.ProcessedOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMongoRepository extends MongoRepository<ProcessedOrder,String> {
}
