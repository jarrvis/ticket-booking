package com.jarrvis.ticketbooking.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ScreeningRepository {

    Mono<Screening> findById(String id);

    Mono<Screening> save(Screening movie);

    Flux<Screening> findByStartTimeBetweenOrderByMovieAscStartTimeAsc(LocalDateTime startDate, LocalDateTime endDate);

    Mono<Boolean> existsByRoomAndStartTimeBeforeAndEndTimeAfter(String room, LocalDateTime startTime, LocalDateTime endTime);

}
