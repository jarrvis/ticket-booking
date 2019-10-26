package com.jarrvis.ticketbooking.domain;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class Screening {

    @NotNull
    private final LocalDateTime startTime;

    @NotNull
    private final LocalDateTime endTime;

    @NotEmpty
    private final String name;

    @NotEmpty
    private final String room;

    @Size(min = 1, max = 30)
    private final int rows;

    @Size(min = 1, max = 30)
    private final int seatsPerRow;

    /**
     * Representing seats per screening as: Table (multi key map)
     *  Key1 / Table row: Row number,
     *  Key2 / Table column: Seat number,
     *  Value: Is place free
     */
    private Table<Integer, Integer, Boolean> seats;


    public boolean isSeatFree(int row, int place) {
        return true;
    }

    public Map<Integer, List<Integer>> availableSeats() {
        return Maps.newHashMap();
    }

    public Table<Integer, Integer, Boolean> allSeats() {
        return seats;
    }

    public void bookSeat(int row, int seat) {
    }

}
