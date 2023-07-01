package br.com.gpds.config;

import br.com.gpds.GpdsApp;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.*;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.apidoc.customizer.JHipsterOpenApiCustomizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_API_DOCS)
@SecuritySchemes({@SecurityScheme(type = SecuritySchemeType.HTTP, name = "bearerAuth", bearerFormat = "JWT", scheme = "bearer")})
public class OpenApiConfiguration {

    public static final String API_FIRST_PACKAGE = "br.com.gpds.web.api";

    @Bean
    @ConditionalOnMissingBean(name = "apiFirstGroupedOpenAPI")
    public GroupedOpenApi apiFirstGroupedOpenAPI(
        JHipsterOpenApiCustomizer jhipsterOpenApiCustomizer,
        JHipsterProperties jHipsterProperties
    ) {
        JHipsterProperties.ApiDocs properties = jHipsterProperties.getApiDocs();
        return GroupedOpenApi
            .builder()
            .group("openapi")
            .addOpenApiCustomizer(jhipsterOpenApiCustomizer)
            .packagesToScan(API_FIRST_PACKAGE)
            .pathsToMatch(properties.getDefaultIncludePattern())
            .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "swaggerUiReplaceUrl")
    public Boolean swaggerUiReplaceUrl(@Value("${springdoc.swagger-ui.url}") String swaggerUiUrl) {
        swaggerUiUrl = swaggerUiUrl.replace("localhost", GpdsApp.hostAddress);
        Resource resource = new ClassPathResource("static/swagger-ui/index.html");
        var htmlSourceContent = "";
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
            htmlSourceContent =
                bufferedReader.lines().collect(Collectors.joining()).replace("https://petstore.swagger.io/v2/swagger.json", swaggerUiUrl);

            Files.write(Paths.get(resource.getFile().getAbsolutePath()), htmlSourceContent.getBytes());
        } catch (IOException | IllegalArgumentException e) {
            //Do nothing
        }
        return Boolean.TRUE;
    }
}
