package io.github.enzogrn.mintbox;

import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Describes the API as a whole in its OpenAPI document. The endpoints themselves are
 * described by springdoc from the controllers and their annotations.
 *
 * <p>The document is served at {@code /v3/api-docs}, and Swagger UI at {@code /swagger-ui.html}.
 *
 * <p>Fields are non-null unless annotated with JSpecify's {@code @Nullable}, like in Spring
 * itself: springdoc documents the annotated ones as nullable (see application.yaml).
 */
@Configuration(proxyBeanMethods = false)
class OpenApiConfiguration {

    private static final String REPOSITORY = "https://github.com/EnzoGrn/mintbox";
    private static final String BASE_PACKAGE = OpenApiConfiguration.class.getPackageName();

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
                        .url(REPOSITORY))
                // A relative server: the API is called on the same origin as the page that
                // uses it, and the document doesn't depend on the host it was generated on.
                .servers(List.of(new Server().url("/").description("Same origin as the web application")));
    }

    /**
     * Marks every property of the API's own records as required: they're always in the JSON,
     * with a {@code null} value when they have none. Without it, OpenAPI considers every
     * property optional, and the types generated from the document make clients check for
     * fields that are always there.
     *
     * <p>Spring's {@code ProblemDetail} is left as is: it leaves out its empty fields.
     */
    @Bean
    ModelConverter requireRecordProperties(ObjectMapperProvider objectMappers) {
        return (type, context, chain) -> {
            if (!chain.hasNext()) {
                return null;
            }
            Schema<?> resolved = chain.next().resolve(type, context, chain);
            Class<?> javaClass = objectMappers.jsonMapper().constructType(type.getType()).getRawClass();
            if (resolved == null || !javaClass.isRecord() || !javaClass.getPackageName().startsWith(BASE_PACKAGE)) {
                return resolved;
            }
            // A model is usually resolved as a reference to its schema in the components.
            Schema<?> model = resolved.get$ref() == null
                    ? resolved
                    : context.getDefinedModels().get(resolved.get$ref().substring(Components.COMPONENTS_SCHEMAS_REF.length()));
            if (model != null && model.getProperties() != null) {
                model.setRequired(model.getProperties().keySet().stream().sorted().toList());
            }
            return resolved;
        };
    }
}
