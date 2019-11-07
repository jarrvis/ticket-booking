package com.jarrvis.ticketbooking.infrastructure.mongo;

import com.jarrvis.ticketbooking.domain.Screening;
import com.jarrvis.ticketbooking.domain.Seat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

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

    @NotEmpty
    private String movie;

    @NotEmpty
    private String room;

    @Range(min = 1, max = 30)
    private Integer rows;

    @Range(min = 1, max = 30)
    private Integer seatsPerRow;

    private Map<Integer, Map<Integer, Seat>> seats;

    public Screening mutateTo() {
        return new Screening(id, startTime, endTime, movie, room, rows, seatsPerRow, seats);
    }

}
