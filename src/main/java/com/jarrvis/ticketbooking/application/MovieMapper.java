package com.jarrvis.ticketbooking.application;


import com.jarrvis.ticketbooking.infrastructure.mongo.MovieDocument;
import com.jarrvis.ticketbooking.infrastructure.mongo.ScreeningDocument;
import com.jarrvis.ticketbooking.ui.dto.response.MovieResource;
import com.jarrvis.ticketbooking.ui.dto.response.ScreeningResource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    List<MovieResource> toMovieResources(List<MovieDocument> screeningDocuments);
    MovieResource toMovieResource(MovieDocument screeningDocument);

}
