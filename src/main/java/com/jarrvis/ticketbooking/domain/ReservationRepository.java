package com.jarrvis.ticketbooking.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ReservationRepository {

    Mono<Reservation> findByIdAndStatusAndToken(String id, ReservationStatus status, String token);
    Flux<Reservation> findByExpiresAtAfter(LocalDateTime dateTime);
    Mono<Reservation> save(Reservation reservation);
    Mono<Reservation> findById(String id);
}
