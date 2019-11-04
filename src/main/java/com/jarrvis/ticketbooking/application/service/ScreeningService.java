package com.jarrvis.ticketbooking.application.service;

import com.jarrvis.ticketbooking.application.mappers.ScreeningMapper;
import com.jarrvis.ticketbooking.domain.Screening;
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieMongoRepository;
import com.jarrvis.ticketbooking.infrastructure.mongo.RoomDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.RoomMongoRepository;
import com.jarrvis.ticketbooking.infrastructure.mongo.ScreeningMongoRepository;
import com.jarrvis.ticketbooking.ui.dto.response.ScreeningResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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

    public Flux<ScreeningResource> searchForScreenings(LocalDateTime startTime, LocalDateTime endTme) {
        return this.screeningRepository.findByStartTimeBetween(startTime, endTme)
                .map(this.screeningMapper::toScreeningResource);
    }

    public Mono<ScreeningResource> addNewScreening(LocalDateTime startTime, String movieName, String roomName) {
        // check if room exists
        final Mono<RoomDocument> room = this.roomMongoRepository.findByName(roomName)
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Room with name: '%s' does not exists", roomName))));

        // check if movie exists
        final Mono<MovieDocument> movie = this.movieMongoRepository.findByName(movieName)
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Movie with name: '%s' does not exists", movieName))));

        // create screening candidate
        final Mono<Screening> screeningMono = Mono.zip(room, movie)
                .flatMap(tuple ->
                        Mono.just(new Screening(
                                startTime,
                                startTime.plusMinutes(tuple.getT2().getDuration()),
                                movieName,
                                roomName,
                                tuple.getT1().getRows(),
                                tuple.getT1().getSeatsPerRow()))
                );


        return screeningMono
                .flatMap(screening -> {
                    // check if screening candidate overlaps with already existing screenings (same room, overlapping hours)
                    return this.screeningRepository.existsByRoomAndStartTimeBeforeAndEndTimeAfter(roomName, screening.getEndTime(), screening.getStartTime())
                            .doOnNext(exists -> {
                                if (exists) {
                                    throw new IllegalStateException("Overlapping screening already exists");
                                }
                            })
                            .flatMap(exists -> Mono.just(screening));
                })
                .flatMap(screening -> Mono.just(this.screeningMapper.toScreeningDocument(screening)))
                .flatMap(this.screeningRepository::save)
                .map(this.screeningMapper::toScreeningResource);
    }

}
