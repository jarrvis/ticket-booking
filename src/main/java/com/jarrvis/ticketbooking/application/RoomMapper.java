package com.jarrvis.ticketbooking.application;


import com.jarrvis.ticketbooking.infrastructure.mongo.RoomDocument;
import com.jarrvis.ticketbooking.ui.dto.response.RoomResource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    List<RoomResource> toRoomResources(List<RoomDocument> roomDocuments);

    RoomResource toRoomResource(RoomDocument roomDocument);

}
