package guru.springframework.spring6restmvc.controller;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist.create;
import static com.atlassian.oai.validator.whitelist.rule.WhitelistRules.messageHasKey;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(BeerControllerRestAssuredTest.TestConfig.class)
@ComponentScan(basePackages = "guru.springframework.spring6restmvc")
public class BeerControllerRestAssuredTest {
    @LocalServerPort
    Integer localPort;

    @MockBean
    private CacheManager manager;

    OpenApiValidationFilter filter = new OpenApiValidationFilter(
            OpenApiInteractionValidator.createForSpecificationUrl("oa3.yml")
                    .withWhitelist(create()
                            .withRule("Ignore date format",
                                    messageHasKey("validation.response.body.schema.format.date-time")
                            )
                            .withRule("Ignore additional properties",
                                    messageHasKey("validation.response.body.schema.additionalProperties")
                            )
                    )
                    .build()
    );

    @BeforeEach
    void setUp() {
        RestAssured.port = localPort;
    }

    @Test
    void testBeers() {
        given().contentType(JSON)
                .when()
                .filter(filter)
                .get("/api/v1/beer")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Configuration
    public static class TestConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http.authorizeHttpRequests(registry -> registry.anyRequest().permitAll())
                    .build();
        }
    }
}
