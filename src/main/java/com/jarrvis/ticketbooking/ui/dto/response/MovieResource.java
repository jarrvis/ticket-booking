package com.jarrvis.ticketbooking.ui.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MovieResource {

    private String name;
    private String description;
    private LocalDateTime firstScreeningDate;
    private LocalDateTime lastScreeningDate;
}
