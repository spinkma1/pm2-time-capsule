package cz.cvut.fel.pm2.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final ClientRegistrationRepository clientRegistrationRepository;

//    private final CustomOAuthSuccessHandler customOAuthSuccessHandler;
    private final JwtRequestFilter jwtRequestFilter;
    private final PreAuthRegisterFilter preAuthRegisterFilter;


    /**
     * Configures CORS settings.
     *
     * @return a configured CorsConfigurationSource
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // Required for OAuth
        // Match all origins dynamically, it is definitely not safe for production.
        configuration.setAllowedOriginPatterns(List.of("*")); // TODO add specific vercel website before PROD release
//        configuration.setAllowedOriginPatterns(List.of("vercel.app/blabla capsule")) ;
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
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // e
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers(SecurityEndpoints.PUBLIC_URLS).permitAll()
                        .requestMatchers(SecurityEndpoints.AUTHENTICATED_URLS).hasAnyAuthority("ROLE_ADMIN", "ROLE_MEMBER")
                        .requestMatchers(SecurityEndpoints.ADMIN_URLS).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(SecurityEndpoints.MEMBER_URLS).hasAuthority("ROLE_MEMBER")
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
//                        .successHandler(customOAuthSuccessHandler)
                    .loginPage("/oauth2/authorization/google")
                    .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                            .oidcUserService(oidcUserService()) // inject your custom user service here
                    )
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())) // For Google SSO logout
                .addFilterBefore(preAuthRegisterFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public HttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);  // Allow semicolons in the URL
        return firewall;
    }

    // Configures logout success handler to revoke Google SSO session
    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("http://localhost:8080/"); // Redirect URI after logout
        return successHandler;
    }


    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
                return super.loadUser(userRequest);
            }};
    }



}
