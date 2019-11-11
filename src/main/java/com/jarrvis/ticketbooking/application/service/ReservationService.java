package com.jarrvis.ticketbooking.application.service;

import com.jarrvis.ticketbooking.domain.Reservation;
import com.jarrvis.ticketbooking.domain.ReservationRepository;
import com.jarrvis.ticketbooking.domain.ReservationStatus;
import com.jarrvis.ticketbooking.domain.Screening;
import com.jarrvis.ticketbooking.domain.ScreeningRepository;
import com.jarrvis.ticketbooking.domain.Ticket;
import com.jarrvis.ticketbooking.ui.dto.response.ReservationResource;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ReservationService {

    public ReservationService(
            final ReservationRepository reservationRepository,
            final ScreeningRepository screeningRepository) {

        this.reservationRepository = reservationRepository;
        this.screeningRepository = screeningRepository;
    }

    private final ReservationRepository reservationRepository;
    private final ScreeningRepository screeningRepository;

    /**
     * @param screeningId
     * @param name
     * @param surname
     * @param tickets
     * @return
     */
    public Mono<ReservationResource> reserve(String screeningId, String name, String surname, Set<Ticket> tickets) {
        Set<Tuple2<Integer, Integer>> places = tickets.stream().map(ticket -> new Tuple2<>(ticket.getRowNumber(), ticket.getSeatNumber())).collect(Collectors.toSet());
        //check if screening exists
        final Mono<Screening> screeningMono = this.screeningRepository.findById(screeningId)
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Screening with id: '%s' does not exists", screeningId))));

        // zipping Reservation and Screening to do operations on both in same methods.
        // E.g. to #1 execute domain functions on both and #2 save both domains in one method not to rely on @Transactional
        // (not to book seats in Screening and save it in one method and create Reservation and save in other
        return
                screeningMono
                        .zipWhen(screening -> Mono.just(new Reservation(screeningId, screening.getStartTime(), name, surname, tickets)))
                        .flatMap(tuple -> {
                            tuple.getT1().bookSeats(places);
                            tuple.getT2().calculateTotalPrice();
                            tuple.getT2().calculateExpirationDate();
                            return Mono.just(tuple);
                        })
                        .flatMap(tuple -> {
                            this.screeningRepository.save(tuple.getT1());
                            return this.reservationRepository.save(tuple.getT2());
                        })
                        .flatMap(reservation ->
                                Mono.just(
                                        new ReservationResource(reservation.getId(), reservation.getToken(), reservation.getStatus(), reservation.getExpiresAt(), reservation.getScreeningId(),
                                                reservation.getScreeningStartTime(), reservation.getName(), reservation.getSurname(), reservation.getFormattedPrice(), reservation.getSeats()))
                        );
    }

    /**
     * @param reservationId
     * @param token
     * @return
     */
    public Mono<ReservationResource> confirm(String reservationId, String token) {
        //check if reservation exists
        final Mono<Reservation> reservation = this.reservationRepository.findByIdAndStatusAndToken(reservationId, ReservationStatus.OPEN, token)
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Open reservation with id: '%s' does not exists", reservationId))));

        //confirm reservation
        return reservation
                .flatMap(domain -> {
                    domain.confirm();
                    return Mono.just(domain);
                })
                .flatMap(domain -> {
                    this.reservationRepository.save(domain);
                    return Mono.just(
                            new ReservationResource(domain.getId(), domain.getToken(), domain.getStatus(), domain.getExpiresAt(), domain.getScreeningId(),
                                    domain.getScreeningStartTime(), domain.getName(), domain.getSurname(), domain.getFormattedPrice(), domain.getSeats()));
                });
    }

    /**
     * @param reservationId
     * @return
     */
    public Mono<Boolean> cancel(String reservationId) {
        //check if reservation exists
        final Mono<Reservation> reservation = this.reservationRepository.findById(reservationId)
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Reservation with id: '%s' does not exists", reservationId))));

        //check if screening exists
        final Mono<Screening> screening = reservation.flatMap(r -> this.screeningRepository.findById(r.getScreeningId()))
                .switchIfEmpty(Mono.error(new IllegalStateException("Screeninig does not exists")));

        return reservation
                .zipWith(screening)
                //cancel reservation
                .flatMap(tuple -> {
                    tuple.getT1().cancel();
                    return this.reservationRepository.save(tuple.getT1())
                            .flatMap(dom -> Mono.just(tuple));
                })
                //free screening places
                .flatMap(tuple -> {
                    tuple.getT2().freeReservedSeats(tuple.getT1().getReservedPlaces());
                    return this.screeningRepository.save(tuple.getT2());
                })
                .flatMap(domain -> Mono.just(true));

    }

    public Mono<Void> cancelExpired() {
        return this.reservationRepository.findByExpiresAtAfter(LocalDateTime.now())
                .flatMap(s -> this.cancel(s.getId()))
                .then();
    }
}