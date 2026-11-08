package io.github.enzogrn.mintbox;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Describes the API as a whole in its OpenAPI document. The endpoints themselves are
 * described by springdoc from the controllers and their annotations.
 *
 * <p>The document is served at {@code /v3/api-docs}, and Swagger UI at {@code /swagger-ui.html}.
 */
@Configuration(proxyBeanMethods = false)
class OpenApiConfiguration {

    private static final String REPOSITORY = "https://github.com/EnzoGrn/mintbox";

    @Bean
    OpenAPI mintboxOpenApi(@Value("${mintbox.version}") String version) {
        return new OpenAPI()
                .info(new Info()
                        .title("Mintbox API")
                        .version(version)
                        .description("""
                                Marketplace and collection tracker for toys-to-life collectible figures.

                                Errors are returned as [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) \
                                problem details, with the `application/problem+json` media type.""")
                        .license(new License().name("MIT").url(REPOSITORY + "/blob/main/LICENSE")))
                .externalDocs(new ExternalDocumentation()
                        .description("Source code, architecture and decision records")
                        .url(REPOSITORY));
    }
}
