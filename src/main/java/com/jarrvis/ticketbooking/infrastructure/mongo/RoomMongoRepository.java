package com.jarrvis.ticketbooking.infrastructure.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface RoomMongoRepository extends ReactiveMongoRepository<RoomDocument, String> {

    Mono<RoomDocument> findByName(String name);
    Mono<Boolean> existsByName(String name);

}
