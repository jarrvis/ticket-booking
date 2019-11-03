package com.jarrvis.ticketbooking.ui.dto.request;

import com.jarrvis.ticketbooking.ui.validation.DateRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DateRange(
        message = "First screening date cannot be after last screening date",
        startDateFieldName = "firstScreeningDate",
        endDateFieldName = "lastScreeningDate")
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
