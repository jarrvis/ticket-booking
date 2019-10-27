package com.jarrvis.ticketbooking.application;

import com.jarrvis.ticketbooking.infrastructure.mongo.RoomDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.RoomMongoRepository;
import com.jarrvis.ticketbooking.ui.dto.response.RoomResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Transactional
public class MovieRoomService {


    public MovieRoomService(
            final RoomMongoRepository roomMongoRepository,
            final RoomMapper roomMapper
    ) {
        this.roomMongoRepository = roomMongoRepository;
        this.roomMapper = roomMapper;
    }

    private final RoomMongoRepository roomMongoRepository;
    private final RoomMapper roomMapper;


    public Mono<RoomDocument> addNewRoom(String name, int rows, int seatsPerRow) {
        RoomDocument room = new RoomDocument(name, rows, seatsPerRow);
        return this.roomMongoRepository.save(room);
    }

    public Flux<RoomResource> listAllRooms() {
        return this.roomMongoRepository.findAll().map(this.roomMapper::toRoomResource);

    }

}
