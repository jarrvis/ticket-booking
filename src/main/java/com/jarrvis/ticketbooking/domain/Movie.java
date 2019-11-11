package com.jarrvis.ticketbooking.domain;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Movie {

    private String name;

    private String description;

    private LocalDateTime firstScreeningDate;

    private LocalDateTime lastScreeningDate;

    private Long duration;
}
