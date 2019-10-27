package com.jarrvis.ticketbooking.application;


import com.jarrvis.ticketbooking.domain.Screening;
import com.jarrvis.ticketbooking.infrastructure.mongo.MovieDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.RoomDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.ScreeningDocument;
import com.jarrvis.ticketbooking.ui.dto.response.ScreeningResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScreeningMapper {

    ScreeningMapper INSTANCE = Mappers.getMapper( ScreeningMapper.class );

    @Mappings({
            @Mapping(source = "movie.name", target = "title"),
    })
    ScreeningResource toScreeningResource(ScreeningDocument screeningDocument);

    @Mappings({
            //@Mapping(source = "movie.name", target = "title"),
    })
    List<ScreeningResource> toScreeningResources(List<ScreeningDocument> screeningDocuments);

    @Mappings(value = {
            @Mapping(source = "movieDocument", target = "movie"),
            @Mapping(source = "roomDocument", target = "room"),
            @Mapping(target = "id", ignore = true),
    })
    ScreeningDocument toScreeningDocument(Screening screening, MovieDocument movieDocument, RoomDocument roomDocument);
}
