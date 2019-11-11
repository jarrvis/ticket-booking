package com.jarrvis.ticketbooking.ui.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreeningResource {

    private String id;
    private String movie;
    private String room;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int rows;
    private int seatsPerRow;
    private Map<Integer, List<Integer>> freePlaces;
}
