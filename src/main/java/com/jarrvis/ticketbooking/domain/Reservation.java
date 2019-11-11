package com.jarrvis.ticketbooking.domain;

import io.vavr.Tuple2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@RequiredArgsConstructor
public class Reservation {


    @Getter
    private String id;

    @Getter
    private String token = UUID.randomUUID().toString();

    @Getter
    private ReservationStatus status = ReservationStatus.OPEN;

    @Getter
    private LocalDateTime expiresAt;

    @Getter
    private LocalDateTime createdAt;

    @Getter
    private final String screeningId;

    @Getter
    private final LocalDateTime screeningStartTime;

    @Getter
    private final String name;

    @Getter
    private final String surname;

    @Getter
    private final Set<Ticket> seats;

    @Getter
    private BigDecimal totalPrice;

    @Getter
    private Currency currency = Currency.PLN;

    public Set<Tuple2<Integer, Integer>> getReservedPlaces() {
        return this.seats.stream().map(ticket -> new Tuple2<>(ticket.rowNumber, ticket.seatNumber)).collect(Collectors.toSet());
    }

    public String getFormattedPrice() {
        if (this.totalPrice == null) {
            this.calculateTotalPrice();
        }
        return new DecimalFormat("#0.##").format(this.totalPrice) + ' ' + this.currency;
    }

    public Price getPrice() {
        if (this.totalPrice == null){
            this.calculateTotalPrice();
        }
        return new Price(this.totalPrice, this.currency);
    }

    public void calculateTotalPrice() {
        if (this.seats == null) {
            this.totalPrice = BigDecimal.ZERO;
        } else {
            this.totalPrice = this.seats.stream().map(ticket -> ticket.ticketType.getValue())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public void calculateExpirationDate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        LocalDateTime quarterAfterReservationTime = this.createdAt.plusMinutes(15);
        LocalDateTime quarterBeforeScreeningTime = screeningStartTime.minusMinutes(15);
        this.expiresAt = quarterAfterReservationTime.isBefore(quarterBeforeScreeningTime) ? quarterAfterReservationTime : quarterBeforeScreeningTime;
        if (LocalDateTime.now().isAfter(this.expiresAt)) {
            throw new IllegalStateException("Reservation already expired");
        }
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    public void confirm() {
        if (this.status == ReservationStatus.CANCELED) {
            throw new IllegalStateException("Cannot confirm reservation. Reservation was already cancelled");
        }
        if (this.status == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot confirm reservation. Reservation was already confirmed");
        }
        if (this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt)) {
            throw new IllegalStateException("Cannot confirm reservation. Reservation already expired");
        }
        this.status = ReservationStatus.CONFIRMED;
    }
}
