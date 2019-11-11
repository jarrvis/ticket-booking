package com.jarrvis.ticketbooking.application.service;

import com.jarrvis.ticketbooking.domain.Movie;
import com.jarrvis.ticketbooking.domain.MovieRepository;
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
@Transactional(rollbackFor = Exception.class)
public class MovieService {


    public MovieService(
            final MovieRepository movieRepository
    ) {
        this.movieRepository = movieRepository;
    }

    private final MovieRepository movieRepository;


    public Mono<Boolean> addNewMovie(String name, String description, LocalDateTime firstScreeningDate, LocalDateTime lastScreeningDate, Long duration) {
        return this.movieRepository.existsByName(name)
                .flatMap(exists -> {
                    if (exists) {
                        throw new AlreadyExistException(String.format("Movie with name: %s already exists", name));
                    }
                    return this.movieRepository.save(new Movie(name, description, firstScreeningDate, lastScreeningDate, duration))
                            .flatMap(movie -> Mono.just(true));
                });
    }

    public Flux<MovieResource> listAllMovies() {
        return this.movieRepository.findAll()
                .flatMap(domain -> Mono.just(new MovieResource(domain.getName(), domain.getDescription(), domain.getFirstScreeningDate(), domain.getLastScreeningDate())));
    }
}
