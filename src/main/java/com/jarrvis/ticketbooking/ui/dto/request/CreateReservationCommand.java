package com.jarrvis.ticketbooking.ui.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationCommand {

    @NotEmpty
    private String screeningId;

    @NotEmpty
    @Pattern(regexp = "^[A-Z].*\\p{L}")
    private String name;

    @NotEmpty
    @Pattern(regexp = "^[A-Z].*\\p{L}((-[A-Z].*\\p{L})?)")
    private String surname;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

}
