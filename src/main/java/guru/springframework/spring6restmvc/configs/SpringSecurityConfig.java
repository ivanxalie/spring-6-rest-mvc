package guru.springframework.spring6restmvc.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        return security
                .csrf(configurer -> configurer
                        .ignoringRequestMatchers("/api/**"))
                .httpBasic(withDefaults())
                .authorizeHttpRequests(registry -> registry.anyRequest().authenticated())
                .build();
    }
}
