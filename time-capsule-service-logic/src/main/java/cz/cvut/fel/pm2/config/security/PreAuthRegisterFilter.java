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

    /**
     * Constructs a PreAuthRegisterFilter with the specified JwtUtil, UserService, and UserRepository.
     *
     * @param jwtUtil the utility class for handling JWTs
     * @param userService the service for user-related operations
     * @param userRepository the repository for user-related database operations
     */
    public PreAuthRegisterFilter(JwtUtil jwtUtil, UserService userService, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Generates a long, secure password.
     *
     * @return a URL-safe, base64-encoded string representing the generated password
     */
    public static String generateLongPassword() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64]; // 64 bytes = 512 bits
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Filters incoming requests to handle pre-authentication registration for SSO.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // used ONLY FOR SSO REGISTRATION

        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);

            try {
                String username = jwtUtil.extractUsername(jwt);


                if (userRepository.findByEmail(username).isPresent()) {
                    filterChain.doFilter(request, response);
                    return;
                }

                if (request.getParameter("password") == null) {
                    String password = generateLongPassword();
                    try {
                        userService.registerUser(password, username);
                    } catch (Exception e) {
                        logger.error("User registration failed during pre-authentication", e);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token or user cannot be registered");
                        return;
                    }
                }
            } catch (Exception e) {

                logger.error("JWT extraction failed during pre-authentication", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token or user cannot be registered");
                return;
            }

            filterChain.doFilter(request, response);
        }
    }
}
