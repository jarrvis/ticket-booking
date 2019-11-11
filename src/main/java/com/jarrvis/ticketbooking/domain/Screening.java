package com.jarrvis.ticketbooking.domain;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.vavr.Tuple2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@AllArgsConstructor
public class Screening {

    @Getter
    private String id;

    @Getter
    private final LocalDateTime startTime;

    @Getter
    private final LocalDateTime endTime;

    @Getter
    private final String movie;

    @Getter
    private final String room;

    @Getter
    private final Integer rows;

    @Getter
    private final Integer seatsPerRow;

    private Table<Integer, Integer, Seat> seats;


    public Screening(String id, LocalDateTime startTime, LocalDateTime endTime, String movie, String room, Integer rows, Integer seatsPerRow, Map<Integer, Map<Integer, Seat>> seats) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.movie = movie;
        this.room = room;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;

        if (seats != null) {
            this.seats = HashBasedTable.create(rows, seatsPerRow);
            seats.forEach((key, value) -> value.forEach((key1, value1) -> this.seats.put(key, key1, value1)));
        }
    }

    public Map<Integer, Map<Integer, Seat>> getSeats() {
        return this.allSeats().rowMap();
    }


    /**
     * Alias for #getSeats
     * Representing seats per screening as: Table (multi key map)
     * Key1 / Table row: Row number,
     * Key2 / Table column: Seat number,
     * Value: Is place free
     */
    /**
     * @return Representation of seats per screening as: Table (multi key map)
     * Key1 / Table row: Row number,
     * Key2 / Table column: Seat number,
     * Value: Is place free
     */
    public Table<Integer, Integer, Seat> allSeats() {
        if (this.seats == null) {
            this.seats = HashBasedTable.create(this.rows, this.seatsPerRow);
            IntStream.range(1, this.rows + 1).boxed()
                    .forEach(row -> IntStream.range(1, this.seatsPerRow + 1).boxed()
                            .forEach(seat -> this.seats.put(row, seat, Seat.FREE)));
        }
        return this.seats;
    }

    /**
     * @param row   number of row
     * @param place number of place in a row
     * @return true if place is not reserved, false otherwise
     */
    public boolean isSeatFree(int row, int place) {
        if (!this.allSeats().contains(row, place)) {
            throw new IllegalArgumentException("No such place");
        }
        return Seat.FREE == this.allSeats().get(row, place);
    }

    /**
     * @param row   number of row
     * @param place number of place in a row
     * @return true if place is reserved, false otherwise
     */
    public boolean isSeatReserved(int row, int place) {
        if (!this.allSeats().contains(row, place)) {
            throw new IllegalArgumentException("No such place");
        }
        return Seat.RESERVED == this.allSeats().get(row, place);
    }

    /**
     * @return Representation of free seats per screening as map:
     * key: row number
     * value: list of free places numbers
     */
    public Map<Integer, List<Integer>> availableSeats() {
        Map<Integer, List<Integer>> freePlaces = new HashMap<>();
        this.allSeats().rowMap().forEach((row, place) -> freePlaces.put(row,
                place.entrySet().stream()
                        .filter(seat -> seat.getValue() == Seat.FREE)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList())
        ));
        return freePlaces;
    }

    /**
     * @param row   number of row
     * @param place number of place in a row
     */
    public void bookSeat(int row, int place) {
        if (!this.isSeatFree(row, place)) {
            throw new IllegalStateException("Seat already booked");
        }
        if (this.isSingleSeatLeftAfterBooking(row, place)) {
            throw new IllegalStateException("Seat cannot be booked since it single seat would be left between two reserved seats");
        }
        this.allSeats().put(row, place, Seat.RESERVED);
    }

    /**
     * @param places set of tuples: _1 -> row number; _2 -> place numbers in row
     */
    public void bookSeats(Set<Tuple2<Integer, Integer>> places) {
        places.forEach(place -> this.bookSeat(place._1, place._2));
    }

    /**
     * @param row   number of row
     * @param place number of place in a row
     */
    public void freeReservedSeat(int row, int place) {
        if (!this.isSeatReserved(row, place)) {
            throw new IllegalStateException("Place not reserved");
        }
        this.allSeats().put(row, place, Seat.FREE);
    }

    /**
     * @param places map representing places to book: key -> row number; value -> list of place numbers in row
     */
    public void freeReservedSeats(Set<Tuple2<Integer, Integer>> places) {
        places.forEach(place -> freeReservedSeat(place._1, place._2));
    }

    /**
     * @param row
     * @param place
     * @return true if single seat would be left between two 'reserved' seats after booking
     * <p>
     * Only 'between two reserved seats' situation is handled as in requirements. No side seats left handling
     */
    private boolean isSingleSeatLeftAfterBooking(int row, int place) {
        if (place + 2 <= this.seatsPerRow) {
            if (isSeatFree(row, place + 1) && !isSeatFree(row, place + 2)) {
                return true;
            }
        }
        if (place - 2 > 0) {
            return isSeatFree(row, place - 1) && !isSeatFree(row, place - 2);
        }
        return false;
    }
}
