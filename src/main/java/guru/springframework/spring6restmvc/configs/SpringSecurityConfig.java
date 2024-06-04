package guru.springframework.spring6restmvc.configs;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Profile("!test")
@Configuration
public class SpringSecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                .authorizeHttpRequests(matcherRegistry ->
                        matcherRegistry
                                .requestMatchers("/v3/api-docs**", "/swagger-ui/**", "/swagger-ui.html")
                                .permitAll())
                .authorizeHttpRequests(matcherRegistry ->
                        matcherRegistry
                                .requestMatchers(EndpointRequest.toAnyEndpoint())
                                .permitAll())
                .authorizeHttpRequests(matcherRegistry ->
                        matcherRegistry.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
                .build();
    }
}
