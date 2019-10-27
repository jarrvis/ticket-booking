package com.jarrvis.ticketbooking.domain;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Screening {

    @NotNull
    private final LocalDateTime startTime;

    @NotNull
    private final LocalDateTime endTime;

    @NotEmpty
    private final String name;

    @NotEmpty
    private final String room;

    @Range(min = 1, max = 30)
    private final Integer rows;

    @Range(min = 1, max = 30)
    private final Integer seatsPerRow;

    private Table<Integer, Integer, Seat> seats;

    @PostConstruct
    public void init() {
        this.seats = HashBasedTable.create(this.rows, this.seatsPerRow);
        IntStream.range(1, this.rows).boxed()
                .forEach(row -> IntStream.range(1, this.seatsPerRow).boxed()
                        .forEach(seat -> this.seats.put(row, seat, Seat.FREE)));
    }

    /**
     * Representing seats per screening as: Table (multi key map)
     * Key1 / Table row: Row number,
     * Key2 / Table column: Seat number,
     * Value: Is place free
     */
    /**
     *
     * @return Representation of seats per screening as: Table (multi key map)
     *  Key1 / Table row: Row number,
     *  Key2 / Table column: Seat number,
     *  Value: Is place free
     */
    public Table<Integer, Integer, Seat> allSeats() {
        return seats;
    }

    /**
     *
     * @param row number of row
     * @param place number of place in a row
     * @return true if place is not reserved, false otherwise
     */
    public boolean isSeatFree(int row, int place) {
        if (!this.seats.contains(row, place)) {
            throw new IllegalArgumentException("No such place");
        }
        return Seat.FREE == this.seats.get(row, place);
    }

    /**
     *
     * @return Representation of free seats per screening as map:
     *  key: row number
     *  value: list of free places numbers
     */
    public Map<Integer, List<Integer>> availableSeats() {
        Map<Integer, List<Integer>> freePlaces = new HashMap<>();
        this.seats.rowMap().forEach((row, place) -> freePlaces.put(row,
                place.entrySet().stream()
                        .filter(seat -> seat.getValue() == Seat.FREE)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList())
        ));
        return freePlaces;
    }

    /**
     *
     * @param row number of row
     * @param place number of place in a row
     */
    public void bookSeat(int row, int place) {
        if (!this.isSeatFree(row, place)) {
            throw new IllegalStateException("Place already booked");
        }
        this.seats.put(row, place, Seat.RESERVED);
    }

}
