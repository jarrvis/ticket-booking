package com.jarrvis.ticketbooking.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningMongoRepository extends MongoRepository<ScreeningDocument, String> {

    List<ScreeningDocument> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

}
