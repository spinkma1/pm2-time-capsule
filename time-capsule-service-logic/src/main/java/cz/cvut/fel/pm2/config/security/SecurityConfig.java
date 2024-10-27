package cz.cvut.fel.pm2.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${cors.allowed-origins:#{null}}")
    private List<String> corsAllowedOrigins;

    /**
     * Configures CORS settings.
     *
     * @return a configured CorsConfigurationSource
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        corsAllowedOriginSet();
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsAllowedOrigins);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures the security filter chain.
     *
     * @param http the HttpSecurity object
     * @return a configured SecurityFilterChain
     * @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers(SecurityEndpoints.PUBLIC_URLS).permitAll()
                        .requestMatchers(SecurityEndpoints.AUTHENTICATED_URLS).hasAnyAuthority("ROLE_ADMIN", "ROLE_MEMBER")
                        .requestMatchers(SecurityEndpoints.ADMIN_URLS).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(SecurityEndpoints.MEMBER_URLS).hasAuthority("ROLE_MEMBER")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }

    /**
     * Configures web security customizations.
     *
     * @return a configured WebSecurityCustomizer
     */
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return web -> web.ignoring()
//                .dispatcherTypeMatchers(DispatcherType.ERROR)
//                .requestMatchers(SecurityEndpoints.PUBLIC_URLS);
//    }

    /**
     * Creates a PasswordEncoder bean.
     *
     * @return a configured PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Validates the CORS allowed origins configuration.
     */
    private void corsAllowedOriginSet() {
        if (corsAllowedOrigins == null || corsAllowedOrigins.isEmpty()) {
            throw new IllegalArgumentException("${cors.allowed-origin} property must be configured");
        }
    }
}
