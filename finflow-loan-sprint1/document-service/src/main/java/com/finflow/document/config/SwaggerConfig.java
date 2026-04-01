package com.finflow.document.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8083");
        devServer.setDescription("Development Server (Direct Access)");

        Server gatewayServer = new Server();
        gatewayServer.setUrl("http://localhost:8080/gateway/documents");
        gatewayServer.setDescription("API Gateway Server");

        return new OpenAPI()
                .info(new Info()
                        .title("FinFlow Document Service API")
                        .version("1.0")
                        .description("Document Management Service for Loan Applications"))
                .servers(List.of(devServer, gatewayServer));
    }
}
