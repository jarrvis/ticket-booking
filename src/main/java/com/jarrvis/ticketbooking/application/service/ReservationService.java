package com.jarrvis.ticketbooking.application.service;

import com.jarrvis.ticketbooking.application.mappers.ReservationMapper;
import com.jarrvis.ticketbooking.application.mappers.ScreeningMapper;
import com.jarrvis.ticketbooking.domain.Reservation;
import com.jarrvis.ticketbooking.domain.ReservationStatus;
import com.jarrvis.ticketbooking.domain.Screening;
import com.jarrvis.ticketbooking.domain.Ticket;
import com.jarrvis.ticketbooking.domain.TicketType;
import com.jarrvis.ticketbooking.infrastructure.mongo.ReservationDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.ReservationMongoRepository;
import com.jarrvis.ticketbooking.infrastructure.mongo.ScreeningDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.ScreeningMongoRepository;
import com.jarrvis.ticketbooking.ui.dto.response.ReservationResource;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ReservationService {

    public ReservationService(
            final ReservationMongoRepository reservationMongoRepository,
            final ReservationMapper reservationMapper,
            final ScreeningMongoRepository screeningRepository,
            final ScreeningMapper screeningMapper) {
        this.reservationMongoRepository = reservationMongoRepository;
        this.reservationMapper = reservationMapper;
        this.screeningRepository = screeningRepository;
        this.screeningMapper = screeningMapper;
    }

    private final ReservationMongoRepository reservationMongoRepository;
    private final ReservationMapper reservationMapper;
    private final ScreeningMongoRepository screeningRepository;
    private final ScreeningMapper screeningMapper;

    /**
     * @param screeningId
     * @param name
     * @param surname
     * @param tickets
     * @return
     */
    public Mono<ReservationResource> reserve(String screeningId, String name, String surname, Set<Tuple3<Integer, Integer, TicketType>> tickets) {
        Set<Tuple2<Integer, Integer>> places = tickets.stream().map(ticket -> new Tuple2<>(ticket._1, ticket._2)).collect(Collectors.toSet());
        Set<Ticket> reservationTickets = tickets.stream().map(ticket -> new Ticket(ticket._1, ticket._2, ticket._3)).collect(Collectors.toSet());
        //check if screening exists
        final Mono<ScreeningDocument> screeningDocument = this.screeningRepository.findById(screeningId)
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Screening with id: '%s' does not exists", screeningId))));

        final Mono<Screening> screening = screeningDocument
                .flatMap(screeningDoc -> Mono.just(screeningDoc.mutateTo()));

        // book places if exist and available (handled in domain)
        final Mono<Reservation> reservation =
                screening
                        .doOnNext(screeningDomain -> screeningDomain.bookSeats(places))
                        .flatMap(domain -> Mono.just(this.screeningMapper.toScreeningDocument(domain)))
                        .flatMap(this.screeningRepository::save)
                        .flatMap(screeningDoc -> Mono.just(new Reservation(screeningId, screeningDoc.getStartTime(), name, surname, reservationTickets)));

        // calculate total price, reservation expiration date and save reservation
        return reservation
                .flatMap(reservationDomain -> {
                    reservationDomain.calculateTotalPrice();
                    reservationDomain.calculateExpirationDate();
                    return Mono.just(reservationDomain);
                })
                .flatMap(domain -> Mono.just(this.reservationMapper.toReservationDocument(domain)))
                .flatMap(this.reservationMongoRepository::save)
                .flatMap(entity -> Mono.just(entity.mutateTo()))
                .map(this.reservationMapper::toReservationResource);
    }

    /**
     * @param reservationId
     * @param token
     * @return
     */
    public Mono<ReservationResource> confirm(String reservationId, String token) {
        //check if reservation exists
        final Mono<ReservationDocument> reservationDocument = this.reservationMongoRepository.findByIdAndStatusAndToken(reservationId, ReservationStatus.OPEN, token)
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Open reservation with id: '%s' does not exists", reservationId))));

        //confirm reservation
        return reservationDocument
                .flatMap(entity -> Mono.just(entity.mutateTo()))
                .doOnNext(Reservation::confirm)
                .flatMap(domain -> Mono.just(this.reservationMapper.toReservationDocument(domain)))
                .flatMap(this.reservationMongoRepository::save)
                .flatMap(entity -> Mono.just(entity.mutateTo()))
                .map(this.reservationMapper::toReservationResource);
    }

    /**
     * @param reservationId
     * @return
     */
    public Mono<Void> cancel(String reservationId) {
        //check if reservation exists
        final Mono<ReservationDocument> reservationDocument = this.reservationMongoRepository.findById(reservationId)
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Reservation with id: '%s' does not exists", reservationId))));

        //check if screening exists
        final Mono<ScreeningDocument> screeningDocument = reservationDocument.flatMap(r -> this.screeningRepository.findById(r.getScreeningId()))
                .switchIfEmpty(Mono.error(new IllegalStateException("Screeninig does not exists")));

        //cancel reservation
        final Mono<Reservation> reservation = reservationDocument
                .flatMap(s -> Mono.just(s.mutateTo()))
                .doOnNext(Reservation::cancel)
                .flatMap(domain -> Mono.just(this.reservationMapper.toReservationDocument(domain)))
                .doOnNext(this.reservationMongoRepository::save)
                .flatMap(entity -> Mono.just(entity.mutateTo()));

        //free screening places
        return screeningDocument
                .flatMap(s -> Mono.just(s.mutateTo()))
                .zipWith(reservation)
                .doOnNext(tuple -> tuple.getT1().freeReservedSeats(tuple.getT2().getReservedPlaces()))
                .then();

    }
}
