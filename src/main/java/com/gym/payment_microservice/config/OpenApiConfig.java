package com.gym.payment_microservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
        @Bean
        public OpenAPI customOpenAPI(
                        @Value("${spring.application.name:Payment API}") String applicationName,
                        @Value("${server.port:8086}") String serverPort) {
                return new OpenAPI()
                                .info(new Info()
                                                .title(applicationName + " - API")
                                                .description("REST API para gestión de pagos del gimnasio.")
                                                .version("1.0.0"))
                                .servers(List.of(
                                                new Server().url("http://localhost:" + serverPort)
                                                                .description("Servidor de Desarrollo Local")));
        }
}
