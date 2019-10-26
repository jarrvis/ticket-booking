package com.jarrvis.ticketbooking.domain

import org.assertj.core.util.Lists
import spock.lang.Specification

import java.time.LocalDateTime


class ScreeningSpec extends Specification {

    def "Can book free place"() {
        given:
            def screening = new Screening(LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Joker", "Dream", 10, 15)
        when:
            screening.bookSeat(1,1)
        then:
            !screening.isSeatFree(1, 1)
    }

    def "Can only books places that exists"() {
        given:
            def screening = new Screening(LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Joker", "Dream", 10, 15)
        when:
            screening.bookSeat(0,0)
        then:
            thrown(IllegalArgumentException)
    }

    def "Can not book place that is already reserved"() {
        given:
            def screening = new Screening(LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Joker", "Dream", 10, 15)
        when:
            screening.bookSeat(1,1)
        and: 'try to book that place again'
            screening.bookSeat(1,1)
        then:
            thrown(IllegalStateException)
    }

    def "Available places test"() {
        given:
            def screening = new Screening(LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Joker", "Dream", 10, 15)
        when: "Booking a few seats"
            screening.bookSeat(1,1)
            screening.bookSeat(1,2)
        then:
            def allSeats = screening.allSeats()
            allSeats.get(1,1) is false
            allSeats.get(1,2) is false
        and:
            def availableSeats = screening.availableSeats()
            availableSeats.get(1, Lists.emptyList()).contains(1) is false
            availableSeats.get(1, Lists.emptyList()).contains(2) is false
    }

}