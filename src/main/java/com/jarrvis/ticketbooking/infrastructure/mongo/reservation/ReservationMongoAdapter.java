package com.jarrvis.ticketbooking.infrastructure.mongo.reservation;

import com.jarrvis.ticketbooking.domain.Reservation;
import com.jarrvis.ticketbooking.domain.ReservationRepository;
import com.jarrvis.ticketbooking.domain.ReservationStatus;
import com.jarrvis.ticketbooking.domain.Room;
import com.jarrvis.ticketbooking.domain.RoomRepository;
import com.jarrvis.ticketbooking.infrastructure.mongo.room.RoomDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.room.RoomMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public class ReservationMongoAdapter implements ReservationRepository {

    private final ReservationMongoRepository reservationMongoRepository;

    public ReservationMongoAdapter(ReservationMongoRepository reservationMongoRepository) {
        this.reservationMongoRepository = reservationMongoRepository;
    }


    @Override
    public Mono<Reservation> findByIdAndStatusAndToken(String id, ReservationStatus status, String token) {
        return this.reservationMongoRepository.findByIdAndStatusAndToken(id, status, token)
                .flatMap(reservationDocument -> Mono.just(reservationDocument.mutateTo()));
    }

    @Override
    public Flux<Reservation> findByExpiresAtAfter(LocalDateTime dateTime) {
        return this.reservationMongoRepository.findByExpiresAtAfter(dateTime)
                .flatMap(reservationDocument -> Mono.just(reservationDocument.mutateTo()));
    }

    @Override
    public Mono<Reservation> save(Reservation reservation) {
        ReservationDocument document =
                new ReservationDocument(
                        reservation.getId(), reservation.getToken(), reservation.getStatus(), reservation.getCreatedAt(),
                        reservation.getScreeningId(), reservation.getScreeningStartTime(), reservation.getName(), reservation.getSurname(),
                        reservation.getSeats(), reservation.getExpiresAt(), reservation.getTotalPrice(), reservation.getCurrency());
        return this.reservationMongoRepository.save(document)
                .flatMap(reservationDocument -> Mono.just(reservationDocument.mutateTo()));
    }

    @Override
    public Mono<Reservation> findById(String id) {
        return this.reservationMongoRepository.findById(id)
                .flatMap(reservationDocument -> Mono.just(reservationDocument.mutateTo()));

    }
}
