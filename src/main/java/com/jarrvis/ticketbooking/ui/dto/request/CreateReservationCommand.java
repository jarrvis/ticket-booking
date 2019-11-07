package com.jarrvis.ticketbooking.ui.dto.request;

import com.jarrvis.ticketbooking.domain.TicketType;
import io.vavr.Tuple3;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationCommand {

    @NotEmpty
    private String screeningId;

    @NotEmpty
    @Pattern(regexp = "([\\p{Lu}][\\p{Ll}]*)")
    private String name;

    @NotEmpty
    @Pattern(regexp = "^([\\p{Lu}][\\p{Ll}]*(-[\\p{Lu}][\\p{Ll}]*)?)")
    private String surname;

    @NotNull
    private Set<Ticket> tickets;

    public Set<Tuple3<Integer,Integer,TicketType>> getTickets() {
        return this.tickets.stream()
                .map(ticket -> new Tuple3<>(ticket.rowNumber, ticket.seatNumber, ticket.ticketType))
                .collect(Collectors.toSet());

    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @NoArgsConstructor
    @Getter
    static class Ticket {

        Integer rowNumber;
        Integer seatNumber;

        @EqualsAndHashCode.Exclude
        TicketType ticketType;
    }

}
