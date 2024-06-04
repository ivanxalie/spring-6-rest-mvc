package guru.springframework.spring6restmvc.configs;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Profile("!test")
@Configuration
public class SpringSecurityConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        return security
                .oauth2ResourceServer(httpSecurity -> httpSecurity.jwt(withDefaults()))
                .authorizeHttpRequests(matcherRegistry -> matcherRegistry.requestMatchers(
                                "/v3/api-docs**", "/swagger-ui/**", "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity security) throws Exception {
        return security
                .authorizeHttpRequests(matcherRegistry -> matcherRegistry.requestMatchers(EndpointRequest.toAnyEndpoint())
                        .permitAll())
                .build();
    }
}
