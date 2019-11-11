package com.jarrvis.ticketbooking.domain;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@ToString(includeFieldNames = false)
public class Price {

    BigDecimal value;
    Currency currency;
}