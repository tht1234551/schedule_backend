package com.jhj.schedule.common.config;


import com.fasterxml.jackson.databind.Module;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullableJackson3Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.type.LogicalType;

@Configuration
@RequiredArgsConstructor
public class ObjectMapperConfig {

    @Bean
    public ObjectMapperProvider springdocObjectMapperProvider(SpringDocConfigProperties springDocConfigProperties) {
        ObjectMapperProvider objectMapperProvider = new ObjectMapperProvider(springDocConfigProperties);
        objectMapperProvider.jsonMapper().registerModule(jacksonNullableJackson2Module());

        return objectMapperProvider;
    }

    @Bean
    public Module jacksonNullableJackson2Module() {
        return new JsonNullableModule();
    }

    @Bean
    public JacksonModule jacksonNullableJackson3Module() {
        return new JsonNullableJackson3Module();
    }

//    @Bean
//    JsonMapperBuilderCustomizer jacksonCustomizer() {
//        return builder -> builder.addModule(jsonNullableModule());
//    }

//    @Bean
//    public JsonMapperBuilderCustomizer enumCoercion() {
//        return builder -> builder
//                .withCoercionConfig(LogicalType.Enum, cfg ->
//                        cfg.setCoercion(CoercionInputShape.EmptyString, CoercionAction.Fail));
//    }

}
