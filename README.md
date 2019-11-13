# ticket-booking

## DDD & TDD

This small repo is a showcase of domain driven development and test driven development. A few DDD concepts implemented here:

- No anemic domain models. Domain objects are 'doing things'. Moreover they do most important things in app
- Application structure: domain, infrastructure, application, ui (aka presentation)
- Application (domain, application service, ui) does not depend on any DB. Persistence layer implementation can be 'replaced' at any time. Currently used MongoDB is only one of implementations that can be utilized
- Services (actually application services, not domain services) are used for executing abstract persistence methods and domain methods
- Use of value objects e.g. Price, Movie, Room
- Use of ports (like application services e.g. ReservationService or repositories e.g. ScreeningRepository) and adapters (like REST endpoint for Screenings or repository implementation e.g. MovieMongoRepository)

For some testing rules see:

https://github.com/jarrvis/ticket-booking/blob/master/src/test/groovy/README.MD

## Run

to run application:

`./gradlew bootRun`

and then to test it:

`curl "http://localhost:8080/screenings?startTime=2019-11-11T14%3A00&endTime=2019-11-11T16%3A30"`
(search for screenings on day 2019-11-11 from 14:00 to 16:30)

or over swagger UI - in your browser go to:

`http://localhost:8080/swagger-ui.html`

### Authorization

some operations require basic auth:

 - user: admin
 - pass: admin
 
 example - to add new movie to multiplex (only admin can do it):
 
 `curl "http://admin:admin@localhost:8080/movies"`
 
 or
 
 `curl "http://localhost:8080/movies" -H "Authorization: Basic YWRtaW46YWRtaW4="`

Accessing swagger ui also requires basic auth. After correct authorization all requests done from swagger ui will automatically carry basic auth header


## Tests

to run all tests:

`./gradlew cleanTest :test --tests '*'`

to run all tests in file:

`./gradlew cleanTest :test --tests "com.jarrvis.ticketbooking.domain.ScreeningSpec"`

to run specific test:

`./gradlew cleanTest :test --tests "com.jarrvis.ticketbooking.domain.ScreeningSpec.Can not book seat leaving one seat free between reserved seats"`

Integration tests are using in memory mongoDB. They (should) cover all possible user journeys and can be used as a sample of how application works

## Init

Apart from integration tests, local manual tests can be done with sample data. To make it simple you can use init script to initialize app with sample data. Tough I encourage to use swagger ui to get operations docs and possible scenarios

Run:

`./gradlew devInit`

Add --debug flag to see requests and responses in console:

`./gradlew devInit --debug`

## TODO

- (Other repo) Move to CQRS with Domain Events as synchronization: two services (source and sink) - to scale separately. Use Kafka/RabbitMQ for pub/sub domain events
- HATEOAS
- Controller tests
- In integration tests: do not use DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD. Replace it with clearing in memory DB before each test

## Requirements

The goal is to build a seat reservation system for a multiplex.
### Business scenario (use case)
1. The user selects the day and the time when he/she would like to see the movie.
2. The system lists movies available in the given time interval - title and screening
times.
3. The user chooses a particular screening.
4. The system gives information regarding screening room and available seats.
5. The user chooses seats, and gives the name of the person doing the reservation
(name and surname).
6. The system gives back the total amount to pay and reservation expiration time.
### Assumptions
1. The system covers a single cinema with multiple rooms (multiplex).
2. Seats can be booked at latest 15 minutes before the screening begins.
3. Screenings given in point 2. of the scenario should be sorted by title and screening
time.
4. There are three ticket types: adult (25 PLN), student (18 PLN), child (12.50 PLN).
### Business requirements
1. The data in the system should be valid, in particular:
a. name and surname should each be at least three characters long, starting
with a capital letter. The surname could consist of two parts separated with a
single dash, in this case the second part should also start with a capital letter.
b. reservation applies to at least one seat.
2. There cannot be a single place left over in a row between two already reserved
places.
3. The system should properly handle Polish characters.
### Technical requirements
1. Application must be written in JVM language (Java, Scala, Kotlin etc.)
2. Operations must be exposed as REST services
3. No need to stick to any particular database - relational, NoSQL or in-memory
database is fine
4. No need to build frontend
### Demo
1. Include shell script that will build and run your app.
2. The system should be automatically initialized with test data (at least three screening
rooms, three movies and two screenings per room).
3. Include shell script that would run whole use case calling respective endpoints (using
e.g. curl), we want to see requests and responses in action.
Before submitting…
1. Make sure your solution contains a README file, which explains how to build and
run your project and demo.
2. If there are some additional assumptions you’ve made, put them in README as well.
3. Prepare a single pull request containing whole source code (so that we can easily do
a code review for you).
### Reservation confirmation
Extension to the main scenario
1. In the last step, in addition to the total amount and reservation expiration time, a
confirmation link (let’s suppose this link would be sent by email in typical system)
should also be given
2. User accesses the link to confirm reservation

Additional requirements
1. If the user does not confirm the reservation in 15 minutes (but not later than 15
minutes before the screening), the system should cancel the reservation.
2. Reservation cancellation should happen in two cases:
a. 15 minutes after the reservation is made,
b. 15 minutes before the screening.

### Additional assumptions
1. Reservation confirmation is done over PATCH /reservations with reservation id and confirmation token. 
Not as a GET thus reservation create operation does not return confirmation url but id and token.
2. Screening rooms are rectangular, no inactive seats
3. Prices are constant (no pricing strategies) and kept in code, not in DB
4. When using Mongo: use version >=  3.7 (handling LocalDateTime), or better >= 4.0 (handling multidocument transactions)
