package com.jarrvis.ticketbooking.application;

import com.jarrvis.ticketbooking.domain.Screening;
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieMongoRepository;
import com.jarrvis.ticketbooking.infrastructure.mongo.RoomDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.RoomMongoRepository;
import com.jarrvis.ticketbooking.infrastructure.mongo.ScreeningDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.ScreeningMongoRepository;
import com.jarrvis.ticketbooking.ui.dto.response.ScreeningResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ScreeningService {


    public ScreeningService(
            final ScreeningMongoRepository screeningRepository,
            final RoomMongoRepository roomMongoRepository,
            final MovieMongoRepository movieMongoRepository,
            final ScreeningMapper screeningMapper
    ) {
        this.screeningRepository = screeningRepository;
        this.roomMongoRepository = roomMongoRepository;
        this.movieMongoRepository = movieMongoRepository;
        this.screeningMapper = screeningMapper;
    }

    private final ScreeningMongoRepository screeningRepository;
    private final RoomMongoRepository roomMongoRepository;
    private final MovieMongoRepository movieMongoRepository;
    private final ScreeningMapper screeningMapper;

    public List<ScreeningResource> find(LocalDateTime startTime, LocalDateTime endTme) {
        List<ScreeningDocument> screeningDocuments = this.screeningRepository.findByStartTimeBetween(startTime, endTme);
        return this.screeningMapper.toScreeningResources(screeningDocuments);
    }

    public void save(String name, LocalDateTime startTime, LocalDateTime endTme, String movieName, String roomName) {
        this.roomMongoRepository.findByName(roomName)
                .doOnSuccess(roomDocument -> {
                    this.movieMongoRepository.findByName(movieName)
                            .doOnSuccess(movieDocument -> {
                                Screening screening = new Screening(startTime, endTme, movieName, roomName, roomDocument.getRows(), roomDocument.getSeatsPerRow());
                                ScreeningDocument screeningDocument = screeningMapper.toScreeningDocument(screening, movieDocument, roomDocument);
                                this.screeningRepository.save(screeningDocument);
                            });
                });
               // .switchIfEmpty(Mono.error(new IllegalStateException("No such room"))




    }

}
