package com.jarrvis.ticketbooking.application.mappers;


import com.jarrvis.ticketbooking.domain.Reservation;
import com.jarrvis.ticketbooking.infrastructure.mongo.ReservationDocument;
import com.jarrvis.ticketbooking.ui.dto.response.ReservationResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mappings({
            @Mapping(target = "totalPrice", expression = "java(reservation.getFormattedPrice())"),
    })
    ReservationResource toReservationResource(Reservation reservation);

    ReservationDocument toReservationDocument(Reservation reservation);
}
