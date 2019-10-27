package com.jarrvis.ticketbooking.ui.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ScreeningResource {

    private String title;
    private LocalDateTime startTime;
    private  LocalDateTime endTime;

}
