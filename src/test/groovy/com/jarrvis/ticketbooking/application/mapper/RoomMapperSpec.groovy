package com.jarrvis.ticketbooking.application.mapper

import com.jarrvis.ticketbooking.application.mappers.RoomMapper
import com.jarrvis.ticketbooking.infrastructure.mongo.RoomDocument
import com.jarrvis.ticketbooking.ui.dto.response.RoomResource
import spock.lang.Specification

class RoomMapperSpec extends Specification {


    private RoomMapper roomMapper


    def setup() {
        roomMapper = RoomMapper.INSTANCE
    }

    def "Room mapper should map fields from entity to resource"() {
        given:
            def name = "Dream"
            def rows = 10
            def seatsPerRow = 15
            def room = new RoomDocument(name, rows, seatsPerRow)

        when:
            RoomResource resource = roomMapper.toRoomResource(room)

        then:
            resource.name == name
            resource.rows == rows
            resource.seatsPerRow == seatsPerRow
    }

    def "Room mapper should map fields from entities to resources"() {
        given:
            def name = "Dream"
            def rows = 10
            def seatsPerRow = 15
            def rooms = [new RoomDocument(name, rows, seatsPerRow)]

        when:
            List<RoomResource> resources = roomMapper.toRoomResources(rooms)

        then:
            resources.first().name == name
            resources.first().rows == rows
            resources.first().seatsPerRow == seatsPerRow
    }
}
