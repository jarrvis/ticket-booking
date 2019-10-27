package com.jarrvis.ticketbooking.ui.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RoomResource {

    private String name;
    private int rows;
    private int seatsPerRow;

}
