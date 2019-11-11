package com.jarrvis.ticketbooking.domain

import org.assertj.core.util.Lists
import spock.lang.Specification

import java.time.LocalDateTime


class ScreeningSpec extends Specification {

    def "Can book free seat"() {
        given:
            def screening = new Screening(LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Joker", "Dream", 10, 15)
        when:
            screening.bookSeat(1, 1)
        then:
            !screening.isSeatFree(1, 1)
    }

    def "Can only books seats that exists"() {
        given:
            def screening = new Screening(LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Joker", "Dream", 10, 15)
        when:
            screening.bookSeat(11, 20)
        then:
            thrown(IllegalArgumentException)
    }

    def "Can not book seat that is already reserved"() {
        given:
            def screening = new Screening(LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Joker", "Dream", 10, 15)
        when:
            screening.bookSeat(1, 1)
        and: 'try to book that seat again'
            screening.bookSeat(1, 1)
        then:
            thrown(IllegalStateException)
    }

    def "Can not book seat leaving one seat free between reserved seats"() {
        given:
            def screening = new Screening(LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Joker", "Dream", 10, 15)
        when: 'booking first seat'
            screening.bookSeat(1, 1)
        and: 'try to book seat again leaving free seat between two reserved'
            screening.bookSeat(1, 3)
        then:
            thrown(IllegalStateException)
    }

    def "Available seats test"() {
        given:
            def screening = new Screening(LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Joker", "Dream", 10, 15)
        when: "Booking a few seats"
            screening.bookSeat(1, 1)
            screening.bookSeat(1, 2)
        then:
            def allSeats = screening.allSeats()
            allSeats.get(1, 1) is Seat.RESERVED
            allSeats.get(1, 2) is Seat.RESERVED
        and:
            def availableSeats = screening.availableSeats()
            !availableSeats.get(1, Lists.emptyList()).contains(1)
            !availableSeats.get(1, Lists.emptyList()).contains(2)
    }

}