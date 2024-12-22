package cz.cvut.fel.pm2.config.security;

import cz.cvut.fel.pm2.repository.UserRepository;
import cz.cvut.fel.pm2.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PreAuthRegisterFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    public PreAuthRegisterFilter(JwtUtil jwtUtil, UserService userService, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public static String generateLongPassword() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64]; // 64 bytes = 512 bits
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        // URL-safe and compact
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);

            try {

                // Extract the email or subject from the token
                String username = jwtUtil.extractUsername(jwt);

                // Check if user exists, and register them if not
                if (userRepository.findByEmail(username).isPresent()) {
                    // Proceed with the rest of the filter chain
                    filterChain.doFilter(request, response);
                    return;
                }
                //generate impossibly long password if the user has none
                String password = generateLongPassword();
                try {
                    userService.registerUser(password, username);
                } catch (Exception e) {
                    // Log and fail gracefully if registration fails
                    logger.error("User registration failed during pre-authentication", e);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token or user cannot be registered");
                    return;
                }

            } catch (Exception e) {
                // Log and fail gracefully if JWT extraction fails
                logger.error("JWT extraction failed during pre-authentication", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token or user cannot be registered");
                return;
            }

            // Proceed with the rest of the filter chain
            filterChain.doFilter(request, response);
        }
    }
}
