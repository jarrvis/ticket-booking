package com.jarrvis.ticketbooking.infrastructure.mongo;

import com.jarrvis.ticketbooking.domain.Currency;
import com.jarrvis.ticketbooking.domain.Reservation;
import com.jarrvis.ticketbooking.domain.ReservationStatus;
import com.jarrvis.ticketbooking.domain.Ticket;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@Document(collection = "reservations")
public class ReservationDocument {

    @Id
    private String id;

    private String token;

    private ReservationStatus status;

    //@CreatedDate
    private LocalDateTime createdAt;

    @NotEmpty
    private String screeningId;

    @NotNull
    private LocalDateTime screeningStartTime;

    @NotEmpty
    private String name;

    @NotEmpty
    private String surname;

    @NotNull
    private Set<Ticket> seats;

    @NotNull
    private LocalDateTime expiresAt;

    @NotNull
    private BigDecimal totalPrice;

    @NotNull
    private Currency currency;

    public Reservation mutateTo() {
        return new Reservation(id, token, status, expiresAt, createdAt, screeningId, screeningStartTime, name, surname, seats, totalPrice, currency);
    }

}
