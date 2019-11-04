package com.jarrvis.ticketbooking.infrastructure.mongo;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Document(collection = "movies")
public class MovieDocument {

    @Id
    public String id;

    @NotEmpty
    @Indexed(unique=true)
    private final String name;

    @NotEmpty
    private final String description;

    @NotNull
    private final LocalDateTime firstScreeningDate;

    @NotNull
    private final LocalDateTime lastScreeningDate;

    @NotNull
    @Range(min=5, max = 200)
    private final Long duration;
}
