package cz.cvut.fel.pm2.config.security;


import cz.cvut.fel.pm2.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtRequestFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String usedAlgorithm = null;
        String username = null;
        String jwt = null;
        boolean usingOAuth2 = false;

        // Log the authorization header to check if JWT is passed correctly
        logger.info("Authorization Header: " + authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt); // Get the username from the JWT
            usedAlgorithm = jwtUtil.getAlgorithm(jwt);
        }

        // Handle OAuth2 Authentication Token (Google SSO)
        if(usedAlgorithm!= null && usedAlgorithm.equals("RS256")){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Log the current Authentication details
            if (authentication != null) {
                logger.info("Current Authentication: " + authentication.getClass().getSimpleName());
                if (authentication instanceof OAuth2AuthenticationToken) {
                    OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
                    logger.info("OAuth2AuthenticationToken principal: " + oauth2Token.getPrincipal());
                    OAuth2User oauth2User = (OAuth2User) oauth2Token.getPrincipal();
                    username = oauth2User.getAttribute("email");  // Assuming 'email' is used as the username

                    // Validate token using RS256 if OAuth2 authentication token
                    String oauthToken = oauth2User.getAttribute("access_token");  // Assuming the access token is attached to the OAuth2User
                    usingOAuth2 = true;

                    if (oauthToken != null) {
                        if (!jwtUtil.validateTokenRS256(oauthToken, userService.loadUserByUsername(username))) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            logger.info("OAuth2 Token validation failed for: " + username);
                            return;
                        }
                        logger.info("OAuth2 Token validated successfully for: " + username);
                    }
                }
            }
        }


        // Additional logging for decision path based on username and JWT
        if (username != null) {
            logger.info("Extracted username: " + username);
        }

        if (usedAlgorithm!= null && usedAlgorithm.equals("HS256")) {
            logger.info("Validating JWT for username: " + username);
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("JWT authentication succeeded for: " + username);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                logger.info("JWT Token validation failed for: " + username);
                return;
            }
        } else if (usedAlgorithm!= null){
            // Handle OAuth2 (SSO) logic
            logger.info("Processing OAuth2 authentication for: " + username);
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("OAuth2 authentication succeeded for: " + username);
            }
        }

        chain.doFilter(request, response);
    }


    //old version
//
//    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
//            throws ServletException, IOException {
//        final String authorizationHeader = request.getHeader("Authorization");
//
//        String username = null;
//        String jwt = null;
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            jwt = authorizationHeader.substring(7);
//            username = jwtUtil.extractUsername(jwt);
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            UserDetails userDetails = this.userService.loadUserByUsername(username);
//
//
//            if (jwtUtil.validateToken(jwt, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//        chain.doFilter(request, response);
//    }
}
