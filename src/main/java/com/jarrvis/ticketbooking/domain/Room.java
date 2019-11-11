package com.jarrvis.ticketbooking.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Room {

    private String name;
    private int rows;
    private int seatsPerRow;
}
