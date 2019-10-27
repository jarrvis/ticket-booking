package com.jarrvis.ticketbooking.ui.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AddNewRoomRequest {

    @NotEmpty
    private String name;

    @Range(min = 1, max = 30)
    private Integer rows;

    @Range(min = 1, max = 30)
    private Integer seatsPerRow;
}
