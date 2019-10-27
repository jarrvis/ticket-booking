package com.jarrvis.ticketbooking.ui.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AddNewMovieRequest {

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    private LocalDateTime firstScreeningDate;

    @NotNull
    private LocalDateTime lastScreeningDate;

}
