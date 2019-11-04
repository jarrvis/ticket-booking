package com.jarrvis.ticketbooking.ui.controller


import com.jarrvis.ticketbooking.infrastructure.mongo.MovieDocument
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieMongoRepository
import com.jarrvis.ticketbooking.infrastructure.mongo.RoomDocument
import com.jarrvis.ticketbooking.infrastructure.mongo.RoomMongoRepository
import com.jarrvis.ticketbooking.ui.configuration.ApplicationConfig
import com.jarrvis.ticketbooking.ui.dto.request.AddNewScreeningRequest
import com.jarrvis.ticketbooking.ui.dto.response.MovieResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.Base64Utils
import reactor.core.publisher.Mono
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static java.nio.charset.StandardCharsets.UTF_8

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ScreeningControllerSpec extends Specification {


    @Autowired
    private WebTestClient webTestClient

    @Autowired
    private ApplicationConfig applicationConfig

    @Autowired
    private MovieMongoRepository movieMongoRepository

    @Autowired
    private RoomMongoRepository roomMongoRepository

    def setup() {

        webTestClient = webTestClient
                .mutate()
                .responseTimeout(Duration.ofMillis(30000))
                .build();
    }

    def "should not be able to change state of screenings without authorization"() {

        when: 'requesting movies api'
            def result = webTestClient
                    .post()
                    .uri("/screenings")
                    .body(Mono.just(AddNewScreeningRequest.builder().build()), AddNewScreeningRequest.class)
                    .exchange()
        then:
            result
                    .expectStatus().isUnauthorized()
    }

    def "should be able to search for screenings without authorization"() {

        when: 'requesting movies api'
            def result = webTestClient
                    .get()
                    .uri({ uriBuilder ->
                        uriBuilder
                                .path("/screenings")
                                .queryParam("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .queryParam("endTime", LocalDateTime.now().plusHours(5).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .build()
                    })
                    .exchange()
        then:
            result
                    .expectStatus().isOk()
    }

    def "should receive 422 status when accessing api with invalid request parameters"() {

        when: 'requesting movies api'
            def result = webTestClient
                    .post()
                    .uri("/screenings")
                    .header("Authorization", "Basic " + Base64Utils
                            .encodeToString((applicationConfig.webAccess.username + ":" + applicationConfig.webAccess.password).getBytes(UTF_8)))
                    .body(Mono.just(AddNewScreeningRequest.builder().build()), AddNewScreeningRequest.class)
                    .exchange()
        then:
            result
                    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)

    }

    def "should not be possible to save screening, if room does not exist"() {
        setup:
            this.movieMongoRepository.save(new MovieDocument("Joker", "Joker", LocalDateTime.now(), LocalDateTime.now().plusDays(20), 120)).block()
            def addNewScreeningRequest = AddNewScreeningRequest.builder()
                    .movieName("Joker")
                    .roomName("Not existing movie")
                    .startTime(LocalDateTime.now())
                    .build()
        when: 'saving screening'
            def result = webTestClient
                    .post()
                    .uri("/screenings")
                    .header("Authorization", "Basic " + Base64Utils
                            .encodeToString((applicationConfig.webAccess.username + ":" + applicationConfig.webAccess.password).getBytes(UTF_8)))

                    .body(Mono.just(addNewScreeningRequest), AddNewScreeningRequest.class)
                    .exchange()
        then:
            result
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    def "should not be possible to save screening, if movie does not exist"() {
        setup:
            this.roomMongoRepository.save(new RoomDocument("Dream", 10, 15)).block()
            def addNewScreeningRequest = AddNewScreeningRequest.builder()
                    .movieName("Not existing movie")
                    .roomName("Dream")
                    .startTime(LocalDateTime.now())
                    .build()
        when: 'saving screening'
            def result = webTestClient
                    .post()
                    .uri("/screenings")
                    .header("Authorization", "Basic " + Base64Utils
                            .encodeToString((applicationConfig.webAccess.username + ":" + applicationConfig.webAccess.password).getBytes(UTF_8)))

                    .body(Mono.just(addNewScreeningRequest), AddNewScreeningRequest.class)
                    .exchange()
        then:
            result
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    def "should be possible to save screening, if movie and room exist"() {
        setup:
            this.movieMongoRepository.save(new MovieDocument("Joker", "Joker", LocalDateTime.now(), LocalDateTime.now().plusDays(20), 120)).block()
            this.roomMongoRepository.save(new RoomDocument("Dream", 10, 15)).block()
            def addNewScreeningRequest = AddNewScreeningRequest.builder()
                    .movieName("Joker")
                    .roomName("Dream")
                    .startTime(LocalDateTime.now())
                    .build()
        when: 'saving screening'
            def result = webTestClient
                    .post()
                    .uri("/screenings")
                    .header("Authorization", "Basic " + Base64Utils
                            .encodeToString((applicationConfig.webAccess.username + ":" + applicationConfig.webAccess.password).getBytes(UTF_8)))

                    .body(Mono.just(addNewScreeningRequest), AddNewScreeningRequest.class)
                    .exchange()
        then:
            result
                    .expectStatus().isCreated()
    }

    def "should not be possible to add screening overlapping with existing screenings. should result in 409 status"() {
        given:
            def startTime = LocalDateTime.now()
            def duration = 120
            def roomName = "Dream"

            def addNewScreeningRequest = AddNewScreeningRequest.builder()
                    .movieName("Joker")
                    .roomName(roomName)
                    .startTime(startTime)
                    .build()
            this.movieMongoRepository.save(new MovieDocument("Joker", "Joker", LocalDateTime.now(), LocalDateTime.now().plusDays(20), duration)).block()
            this.roomMongoRepository.save(new RoomDocument(roomName, 10, 15)).block()
        when: 'saving screening 1'
            def result = webTestClient
                    .post()
                    .uri("/screenings")
                    .header("Authorization", "Basic " + Base64Utils
                            .encodeToString((applicationConfig.webAccess.username + ":" + applicationConfig.webAccess.password).getBytes(UTF_8)))

                    .body(Mono.just(addNewScreeningRequest), AddNewScreeningRequest.class)
                    .exchange()
        then:
            result
                    .expectStatus().isCreated()
        and: 'saving screening 2'
            def addNewScreeningRequest2 = AddNewScreeningRequest.builder()
                    .movieName("Joker")
                    .roomName(roomName)
                    .startTime(startTime.plusMinutes(duration / 2 as long))
                    .build()

            def result2 = webTestClient
                    .post()
                    .uri("/screenings")
                    .header("Authorization", "Basic " + Base64Utils
                            .encodeToString((applicationConfig.webAccess.username + ":" + applicationConfig.webAccess.password).getBytes(UTF_8)))

                    .body(Mono.just(addNewScreeningRequest2), AddNewScreeningRequest.class)
                    .exchange()
        then:
            result2
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
    }

    def "should be possible to save screening and query it later"() {
        given:
            def startTime = LocalDateTime.now()
            def duration = 120
            def addNewScreeningRequest = AddNewScreeningRequest.builder()
                    .movieName("Joker")
                    .roomName("Dream")
                    .startTime(startTime)
                    .build()
            this.movieMongoRepository.save(new MovieDocument("Joker", "Joker", LocalDateTime.now(), LocalDateTime.now().plusDays(20), duration)).block()
            this.roomMongoRepository.save(new RoomDocument("Dream", 10, 15)).block()
        when: 'saving screening'
            def result = webTestClient
                    .post()
                    .uri("/screenings")
                    .header("Authorization", "Basic " + Base64Utils
                            .encodeToString((applicationConfig.webAccess.username + ":" + applicationConfig.webAccess.password).getBytes(UTF_8)))

                    .body(Mono.just(addNewScreeningRequest), AddNewScreeningRequest.class)
                    .exchange()
        then:
            result
                    .expectStatus().isCreated()
        and:
            def queryResult = webTestClient
                    .get()
                    .uri({ uriBuilder ->
                        uriBuilder
                                .path("/screenings")
                                .queryParam("startTime", startTime.minusHours(5).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .queryParam("endTime", startTime.plusHours(5).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .build()
                    })
                    .exchange()
        then:
            queryResult
                    .expectStatus().isOk()
                    .expectBodyList(MovieResource.class).consumeWith({ list -> assert list.getResponseBody().size() == 1 });

    }

}