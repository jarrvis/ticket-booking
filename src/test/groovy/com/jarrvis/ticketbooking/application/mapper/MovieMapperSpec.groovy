package com.jarrvis.ticketbooking.application.mapper

import com.jarrvis.ticketbooking.application.mappers.MovieMapper
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieDocument
import com.jarrvis.ticketbooking.ui.dto.response.MovieResource
import spock.lang.Specification

import java.time.LocalDateTime

class MovieMapperSpec extends Specification {


    private MovieMapper movieMapper


    def setup() {
        movieMapper = MovieMapper.INSTANCE
    }

    def "Movie mapper should map fields from entity to resource"() {
        given:
            def name = "Joker"
            def description = "Joker"
            def firstScreeningDate = LocalDateTime.now().minusDays(2)
            def lastScreeningDate = LocalDateTime.now().plusDays(20)
            def movie = new MovieDocument(name, description, firstScreeningDate, lastScreeningDate, 120)

        when:
            MovieResource resource = movieMapper.toMovieResource(movie)

        then:
            resource.name == name
            resource.description == description
            resource.firstScreeningDate == firstScreeningDate
            resource.lastScreeningDate == lastScreeningDate
    }

    def "Movie mapper should map fields from entities to resources"() {
        given:
            def name = "Joker"
            def description = "Joker"
            def firstScreeningDate = LocalDateTime.now().minusDays(2)
            def lastScreeningDate = LocalDateTime.now().plusDays(20)
            def movies = [new MovieDocument(name, description, firstScreeningDate, lastScreeningDate, 120)]

        when:
            List<MovieResource> resources = movieMapper.toMovieResources(movies)

        then:
            resources.first().name == name
            resources.first().description == description
            resources.first().firstScreeningDate == firstScreeningDate
            resources.first().lastScreeningDate == lastScreeningDate
    }
}
