package com.jarrvis.ticketbooking.ui.dto.request


import spock.lang.Specification
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory
import java.time.LocalDateTime

class AddNewMovieRequestSpec extends Specification {

    private static Validator validator

    def setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
        validator = factory.getValidator()

    }

    def "should not be possible to add new movie without movie name"() {
        setup:
            def addNewMovieRequest = AddNewMovieRequest.builder()
                    .name("")
                    .description("Nice movie")
                    .firstScreeningDate(LocalDateTime.now())
                    .lastScreeningDate(LocalDateTime.now().plusHours(2)).build()

        when:
            Set<ConstraintViolation<AddNewMovieRequest>> validationResults = validator.validate(addNewMovieRequest)
        then:
            validationResults.first().getPropertyPath().first().name == "name"
    }

    def "should not be possible to add new movie without movie description"() {
        setup:
            def addNewMovieRequest = AddNewMovieRequest.builder()
                    .name("Joker")
                    .description("")
                    .firstScreeningDate(LocalDateTime.now())
                    .lastScreeningDate(LocalDateTime.now().plusHours(2)).build()

        when:
            Set<ConstraintViolation<AddNewMovieRequest>> validationResults = validator.validate(addNewMovieRequest)
        then:
            validationResults.first().getPropertyPath().first().name == "description"
    }

    def "should not be possible to add new movie without movie start time or end time"() {
        setup:
            def addNewMovieRequest = AddNewMovieRequest.builder()
                    .name("Joker")
                    .description("Joker")
                    .firstScreeningDate(null)
                    .lastScreeningDate(null).build()

        when:
            Set<ConstraintViolation<AddNewMovieRequest>> validationResults = validator.validate(addNewMovieRequest)
        then:
            validationResults.any { it.getPropertyPath().first().name == "firstScreeningDate" }
            validationResults.any { it.getPropertyPath().first().name == "lastScreeningDate" }
    }

    def "should not be possible to add new movie with start time after end time"() {
        setup:
            def addNewMovieRequest = AddNewMovieRequest.builder()
                    .name("Joker")
                    .description("Joker")
                    .firstScreeningDate(LocalDateTime.now())
                    .lastScreeningDate(LocalDateTime.now().minusHours(2)).build()

        when:
            Set<ConstraintViolation<AddNewMovieRequest>> validationResults = validator.validate(addNewMovieRequest)
        then:
            validationResults.any { it.messageTemplate == "First screening date cannot be after last screening date" }

    }
}