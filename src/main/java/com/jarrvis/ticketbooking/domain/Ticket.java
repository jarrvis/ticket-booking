package com.jarrvis.ticketbooking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Ticket {

    Integer rowNumber;
    Integer seatNumber;

    @EqualsAndHashCode.Exclude
    TicketType ticketType;
}
