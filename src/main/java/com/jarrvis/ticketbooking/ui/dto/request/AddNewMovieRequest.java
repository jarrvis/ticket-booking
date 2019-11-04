package com.jarrvis.ticketbooking.ui.dto.request;

import com.jarrvis.ticketbooking.ui.validation.DateRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime firstScreeningDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastScreeningDate;

    @NotNull
    @Range(min = 5, max = 200)
    private Long duration;

}
