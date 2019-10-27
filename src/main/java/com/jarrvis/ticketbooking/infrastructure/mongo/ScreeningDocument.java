package com.jarrvis.ticketbooking.infrastructure.mongo;

import com.google.common.collect.Table;
import com.jarrvis.ticketbooking.domain.Screening;
import com.jarrvis.ticketbooking.domain.Seat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "screenings")
public class ScreeningDocument {

    @Id
    public String id;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private MovieDocument movie;

    @NotNull
    private RoomDocument room;

    private Table<Integer, Integer, Seat> seats;

    Screening translate() {
        return new Screening(startTime, endTime, this.movie.getName(), this.room.getName(), this.room.getRows(), this.room.getSeatsPerRow(), seats);
    }
}
