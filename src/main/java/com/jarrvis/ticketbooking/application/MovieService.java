package com.jarrvis.ticketbooking.application;

import com.jarrvis.ticketbooking.infrastructure.mongo.MovieDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieMongoRepository;
import com.jarrvis.ticketbooking.ui.dto.response.MovieResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

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


    public Mono<MovieDocument> addNewMovie(String name, String description, LocalDateTime firstScreeningDate, LocalDateTime lastScreeningDate) {
        MovieDocument movie = new MovieDocument(name, description, firstScreeningDate, lastScreeningDate);
        return this.movieMongoRepository.save(movie);
    }

    public Flux<MovieResource> listAllMovies() {
        return this.movieMongoRepository.findAll()
                .map(movieMapper::toMovieResource);
    }
}
