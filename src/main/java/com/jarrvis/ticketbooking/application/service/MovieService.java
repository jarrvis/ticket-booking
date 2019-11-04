package com.jarrvis.ticketbooking.application.service;

import com.jarrvis.ticketbooking.application.mappers.MovieMapper;
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieMongoRepository;
import com.jarrvis.ticketbooking.ui.dto.response.MovieResource;
import com.jarrvis.ticketbooking.ui.exception.AlreadyExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class MovieService {


    public MovieService(
            final MovieMongoRepository movieMongoRepository,
            final MovieMapper movieMapper
    ) {
        this.movieMongoRepository = movieMongoRepository;
        this.movieMapper = movieMapper;
    }

    private final MovieMongoRepository movieMongoRepository;
    private final MovieMapper movieMapper;


    public Mono<MovieDocument> addNewMovie(String name, String description, LocalDateTime firstScreeningDate, LocalDateTime lastScreeningDate, Long duration) {
        return this.movieMongoRepository.existsByName(name)
                .doOnNext(exists -> {
                    if (exists) {
                        throw new AlreadyExistException(String.format("Movie with name: %s already exists", name));
                    }
                })
                .flatMap(exists -> Mono.just(new MovieDocument(name, description, firstScreeningDate, lastScreeningDate,duration))
                .flatMap(this.movieMongoRepository::save));
    }

    public Flux<MovieResource> listAllMovies() {
        return this.movieMongoRepository.findAll()
                .map(movieMapper::toMovieResource);
    }
}
