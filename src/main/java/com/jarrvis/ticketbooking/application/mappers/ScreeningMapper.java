package com.jarrvis.ticketbooking.application.mappers;


import com.jarrvis.ticketbooking.domain.Screening;
import com.jarrvis.ticketbooking.infrastructure.mongo.ScreeningDocument;
import com.jarrvis.ticketbooking.ui.dto.response.ScreeningResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScreeningMapper {

    ScreeningMapper INSTANCE = Mappers.getMapper( ScreeningMapper.class );


    ScreeningResource toScreeningResource(ScreeningDocument screeningDocument);

    @Mappings({
            //@Mapping(source = "movie.name", target = "title"),
    })
    List<ScreeningResource> toScreeningResources(List<ScreeningDocument> screeningDocuments);


    ScreeningDocument toScreeningDocument(Screening screening);
}
