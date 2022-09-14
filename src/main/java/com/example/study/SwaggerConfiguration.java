package com.example.study;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SwaggerConfiguration {

    private ApiInfo swaggerInfo(String title, String version) {
        return new ApiInfoBuilder().title(title)
                .description("Study space "+ version+ " Docs")
                .version(version)
                .build();
    }

    @Bean
    public Docket swaggerApiV1() {
        String version = "V1";
        return new Docket(DocumentationType.OAS_30)
                .groupName(version)
                .consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
                .apiInfo(swaggerInfo("Study space", version)).select()
                .apis(RequestHandlerSelectors.basePackage("com.example.study.api"))
                .paths(PathSelectors.ant("/api/v1/**"))
                .build()
                .useDefaultResponseMessages(false);
    }

    @Bean
    public Docket swaggerApiV2() {
        String version = "V2";
        return new Docket(DocumentationType.OAS_30)
                .groupName(version)
                .consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
                .apiInfo(swaggerInfo("Study space", "V2")).select()
                .apis(RequestHandlerSelectors.basePackage("com.example.study.api"))
                .paths(PathSelectors.ant("/api/v2/**"))
                .build()
                .useDefaultResponseMessages(false);
    }

    private Set<String> getConsumeContentTypes() {
        return new HashSet<>() {{
            add("application/json;charset=UTF-8");
            add("application/x-www-form-urlencoded");
        }};
    }

    private Set<String> getProduceContentTypes() {
        return new HashSet<>() {{
            add("application/json;charset=UTF-8");
        }};
    }
}