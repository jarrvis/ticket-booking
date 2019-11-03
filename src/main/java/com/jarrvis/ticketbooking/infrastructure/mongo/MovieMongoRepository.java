package com.jarrvis.ticketbooking.infrastructure.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface MovieMongoRepository extends ReactiveMongoRepository<MovieDocument, String> {

    Mono<MovieDocument> findByName(String name);
    Mono<Boolean> existsByName(String name);

}
