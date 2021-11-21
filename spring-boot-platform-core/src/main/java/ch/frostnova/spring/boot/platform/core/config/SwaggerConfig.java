package ch.frostnova.spring.boot.platform.core.config;

import ch.frostnova.spring.boot.platform.core.ApplicationInfoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static java.util.Collections.emptyList;

/**
 * Swagger configuration.
 */
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    @Autowired
    private ApplicationInfoProperties applicationInfoProperties;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.any())
                .build().apiInfo(getApiInfo());

    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                applicationInfoProperties.getName(),
                applicationInfoProperties.getDescription(),
                applicationInfoProperties.getVersion(),
                null,
                null,
                null,
                null,
                emptyList()
        );
    }
}