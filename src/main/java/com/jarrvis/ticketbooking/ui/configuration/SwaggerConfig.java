package com.jarrvis.ticketbooking.ui.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Main configuration class to enable the Swagger UI frontend. It registers all supported controllers and describes the
 * API.
 * 
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(or( regex("/.*movies.*"), regex("/.*rooms.*")))
                .build()
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "ticket booking REST API",
                "Multiplex ticket booking.",
                "1.0.0-dev",
"",
                new Contact("Michal Kaliszewski", "https://github.com/jarrvis", "michal.kaliszewski00@gmail.com"),
                "License of API", "API license URL", Collections.emptyList());
    }
}