package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    private SecurityScheme createRefreshTokenScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .name("Refresh-Token");
    }

    public OpenApiCustomizer createOpenApiCustomizer(String title, String version) {
        return openApi -> {
            openApi.info(new Info().title(title).version(version));

            openApi.schemaRequirement("bearerAuth", createAPIKeyScheme());
            openApi.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

            openApi.schemaRequirement("refreshToken", createRefreshTokenScheme());
            openApi.addSecurityItem(new SecurityRequirement().addList("refreshToken"));

            openApi.addServersItem(new Server()
                    .url("https://went_back.gamza.club")
                    .description("Test Server"));
//            openApi.addServersItem(new Server()
//                    .url("http://3.34.207.58:80")
//                    .description("TestServer Server"));
        };
    }
//    @Bean
//    public OpenAPI openAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("다녀왔습니다")
//                        .description("다녀왔습니다 API")
//                        .version("v1.0.0"));
//    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/**")
                .displayName("다녀왔습니다 API")
                .addOpenApiCustomizer(createOpenApiCustomizer("다녀왔습니다", "v0.1"))
                .build();
    }
}
