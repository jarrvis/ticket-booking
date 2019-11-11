package com.jarrvis.ticketbooking.domain

import spock.lang.Specification

import java.time.LocalDateTime


class ReservationSpec extends Specification {

    def "Should calculate total price correctly"() {
        given:
            def reservation = new Reservation("1234", LocalDateTime.now().plusDays(1), "Tony", "Stark",
                    [
                            new Ticket(5, 5, TicketType.ADULT),
                            new Ticket(5, 6, TicketType.CHILD),
                            new Ticket(5, 7, TicketType.STUDENT),
                    ] as Set)
        when:
            reservation.calculateTotalPrice()
        then:
            reservation.totalPrice == TicketType.ADULT.value + TicketType.CHILD.value + TicketType.STUDENT.value
    }

    def "Should calculate expiration date correctly"() {
        given: 'Reservation for screening that start tomorrow'
            def reservation = new Reservation("1234", LocalDateTime.now().plusDays(1), "Tony", "Stark",
                    [
                            new Ticket(5, 5, TicketType.ADULT),
                    ] as Set)
        when:
            def justBeforeReservation = LocalDateTime.now()
            reservation.calculateExpirationDate()
            def rightAfterReservation = LocalDateTime.now()
        then:
            (reservation.expiresAt.isEqual(justBeforeReservation.plusMinutes(15)) || reservation.expiresAt.isAfter(justBeforeReservation.plusMinutes(15))) &&
                    (reservation.expiresAt.isEqual(rightAfterReservation.plusMinutes(15)) || reservation.expiresAt.isBefore(rightAfterReservation.plusMinutes(15)))

    }

    def "Should cancel reservation"() {
        given:
            def reservation = new Reservation("1234", LocalDateTime.now().plusDays(1), "Tony", "Stark",
                    [
                            new Ticket(5, 5, TicketType.ADULT),
                    ] as Set)
        when:
            reservation.cancel()
        then:
            reservation.status == ReservationStatus.CANCELED
    }

    def "Should confirm open reservation"() {
        given:
            def reservation = new Reservation("1234", LocalDateTime.now().plusDays(1), "Tony", "Stark",
                    [
                            new Ticket(5, 5, TicketType.ADULT),
                    ] as Set)
        when:
            reservation.confirm()
        then:
            reservation.status == ReservationStatus.CONFIRMED
    }

    def "Should not be able to confirm cancelled reservation"() {
        given:
            def reservation = new Reservation("1234", LocalDateTime.now().plusDays(1), "Tony", "Stark",
                    [
                            new Ticket(5, 5, TicketType.ADULT),
                    ] as Set)
        when:
            reservation.cancel()
        and:
            reservation.confirm()
        then:
            thrown(IllegalStateException)
    }

    def "Should not be able to confirm already confirmed reservation"() {
        given:
            def reservation = new Reservation("1234", LocalDateTime.now().plusDays(1), "Tony", "Stark",
                    [
                            new Ticket(5, 5, TicketType.ADULT),
                    ] as Set)
        when:
            reservation.confirm()
        and:
            reservation.confirm()
        then:
            thrown(IllegalStateException)
    }

    def "Should not be able to confirm expired reservation"() {
        given:
            def reservation = new Reservation("1234", LocalDateTime.now().minusHours(1), "Tony", "Stark",
                    [
                            new Ticket(5, 5, TicketType.ADULT),
                    ] as Set)
        when:
            reservation.calculateExpirationDate()
            reservation.confirm()
        then:
            thrown(IllegalStateException)
    }
}