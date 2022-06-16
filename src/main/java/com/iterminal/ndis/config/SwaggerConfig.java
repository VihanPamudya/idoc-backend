package com.iterminal.ndis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com.iterminal"))
                .build()
                .apiInfo(apiDetails());

        return docket;
    }

    private ApiInfo apiDetails() {
        return new ApiInfo(
                "National Development Information System",
                "API Documentation for NDIS",
                "1.0.0",
                "Free to use",
                new springfox.documentation.service.Contact("Navishka Kularathna", "", "navishkar@iterminaltech.net"),
                "API Licence",
                "https://iterminaltech.net/index.html",
                Collections.emptyList());
    }
}
