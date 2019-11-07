package com.jarrvis.ticketbooking.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Ticket {

    Integer rowNumber;
    Integer seatNumber;

    @EqualsAndHashCode.Exclude
    TicketType ticketType;
}
