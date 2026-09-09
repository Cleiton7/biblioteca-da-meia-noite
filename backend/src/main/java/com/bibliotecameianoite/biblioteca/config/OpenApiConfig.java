package com.bibliotecameianoite.biblioteca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação OpenAPI/Swagger da API.
 * Disponibiliza a interface interativa em /swagger-ui.html, permitindo
 * visualizar e testar os endpoints REST expostos pelo backend.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bibliotecaMeiaNoiteOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Biblioteca da Meia Noite - API")
                        .description("Documentação da API REST do sistema de gerenciamento "
                                + "de biblioteca Biblioteca da Meia Noite. "
                                + "Permite gerenciar livros, autores, usuários e empréstimos.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Biblioteca da Meia Noite")
                                .url("https://github.com/Cleiton7/biblioteca-da-meia-noite")));
    }
}
