package com.facenet.mdm.custom;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class CustomObjectMapper {

    //    @Bean
    //    public ObjectMapper objectMapper() {
    //        ObjectMapper mapper = new ObjectMapper();
    //        mapper.setAnnotationIntrospector(new SecuredFieldIntrospector());
    //        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    //
    ////        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
    //        return mapper;
    //    }

    @Bean
    Jackson2ObjectMapperBuilderCustomizer objectMapperBuilder() {
        return builder -> builder.annotationIntrospector(new SecuredFieldIntrospector());
        //        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        //        // Configure the builder to suit your needs
        //        builder.failOnUnknownProperties(false);
        //        builder.
        //        builder.annotationIntrospector(new SecuredFieldIntrospector());
        //        return builder;
    }
}
