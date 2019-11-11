package com.jarrvis.ticketbooking.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Movie {

    private String name;

    private String description;

    private LocalDateTime firstScreeningDate;

    private LocalDateTime lastScreeningDate;

    private Long duration;
}
