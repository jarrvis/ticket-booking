package com.jarrvis.ticketbooking.application

import com.jarrvis.ticketbooking.domain.Screening
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieDocument
import com.jarrvis.ticketbooking.infrastructure.mongo.RoomDocument
import com.jarrvis.ticketbooking.infrastructure.mongo.ScreeningDocument
import spock.lang.Specification

import java.time.LocalDateTime

class ScreeningMapperSpec extends Specification {


    private ScreeningMapper screeningMapper


    def setup() {
        screeningMapper = ScreeningMapper.INSTANCE
    }

    def "Screening mapper should map fields from domain to document"() {
        given:
            def startTime = LocalDateTime.now()
            def endTime = LocalDateTime.now().plusHours(2)
            def screening = new Screening(startTime, endTime, "Joker", "Dream", 10, 10)
            def movie = new MovieDocument("Joker", "Joker", LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(20))
            def room = new RoomDocument("Dream", 10, 10)

        when:
            ScreeningDocument screeningDocument = screeningMapper.toScreeningDocument(screening, movie, room)

        then:
            screeningDocument.movie == movie
            screeningDocument.room == room
            screeningDocument.startTime == startTime
            screeningDocument.endTime == endTime
    }
}
