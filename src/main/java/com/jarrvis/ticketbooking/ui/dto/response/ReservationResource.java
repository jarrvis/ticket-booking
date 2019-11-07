package com.jarrvis.ticketbooking.ui.dto.response;

import com.jarrvis.ticketbooking.domain.ReservationStatus;
import com.jarrvis.ticketbooking.domain.Ticket;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class ReservationResource {

    private String token;
    private ReservationStatus status;
    private LocalDateTime expiresAt;
    private String screeningId;
    private LocalDateTime screeningStartTime;
    private String name;
    private String surname;
    private String totalPrice;
    private Set<Ticket> seats;

}
