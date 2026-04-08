package br.com.devl.mfc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API MFC")
                .version("1.0"))
                .addServersItem(new Server().url("http://localhost:8090").description("Local"))
                .addServersItem(new Server().url("https://api.mfc.devl.com.br").description("Produção"))
                .addServersItem(new Server().url("http://api.mfc.devl.com.br").description("Desenvolvimento"))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}