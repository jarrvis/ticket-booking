package com.jarrvis.ticketbooking.ui.dto.request;

import com.jarrvis.ticketbooking.ui.validation.DateRange;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "Movie name.", required = true)
    private String name;

    @NotEmpty
    @ApiModelProperty(value = "Movie description.", required = true)
    private String description;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @ApiModelProperty(value = "First screening date - meta.", required = true)
    private LocalDateTime firstScreeningDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @ApiModelProperty(value = "Last screening date - meta.", required = true)
    private LocalDateTime lastScreeningDate;

    @NotNull
    @Range(min = 5, max = 200)
    @ApiModelProperty(value = "Movie duration in minutes.", required = true)
    private Long duration;

}
