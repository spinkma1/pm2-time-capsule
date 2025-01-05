package cz.cvut.fel.pm2.api;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Product;
import cz.cvut.fel.pm2.config.security.JwtUtil;
import cz.cvut.fel.pm2.model.PasswordChangeRequest;
import cz.cvut.fel.pm2.model.RefreshTokenRequestDto;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.UserRepository;
import cz.cvut.fel.pm2.service.StripeService;
import cz.cvut.fel.pm2.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;



import java.util.*;

@RestController
@RequiredArgsConstructor
public class UserApiImpl implements UserApi {

    @Autowired
    private UserRepository userRepository;
    private final UserService userService;
    private final StripeService stripeService;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Handles SSO login.
     *
     * @param principal the authenticated principal
     * @return a response entity containing login information
     */
    @Override
    @GetMapping(value = "/login/sso", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody()
    public ResponseEntity<Map<String, String>> login(@AuthenticationPrincipal(errorOnInvalidType = true) Object principal) {
        Map<String, String> response = new HashMap<>();

        if (principal instanceof OidcUser oidcUser) {
            response.put("id", oidcUser.getSubject());
            response.put("name", oidcUser.getFullName());
            response.put("email", oidcUser.getEmail());
            response.put("message", "Login successful via SSO");
        } else if (principal instanceof UserDetails userDetails) {
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            response.put("message", "Login successful");
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("email", userDetails.getUsername());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not authenticated");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves user information.
     *
     * @param oidcUser the authenticated OIDC user
     * @return a map containing user information
     */
    @Override
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        Map<String, Object> response = new HashMap<>();
        if (oidcUser != null) {
            String userId = oidcUser.getSubject();
            response.put("id", userId);
            response.put("name", oidcUser.getFullName());
            response.put("email", oidcUser.getEmail());
            response.put("capsules", userService.getCapsules(userId));
            response.put("followers", userService.getFollowers(userId));
        } else {
            response.put("message", "User not authenticated");
        }
        return response;
    }

    /**
     * Handles SSO registration.
     *
     * @param oidcUser the authenticated OIDC user
     * @return a response entity containing registration information
     */
    @PostMapping("/register/sso")
    public ResponseEntity<Map<String, Object>> register(@AuthenticationPrincipal OidcUser oidcUser) {
        Map<String, Object> response = new HashMap<>();

        if (oidcUser != null) {
            String email = oidcUser.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, null));
            UserDetails userDetails = userService.loadUserByUsername(email);
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            response.put("message", "Login successful");
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("email", email);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Handles login with email and password.
     *
     * @param request the login request containing email and password
     * @return a response entity containing login information
     * @throws IllegalAccessException if the login fails
     */
    @Override
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) throws IllegalAccessException {
        String email = request.get("email");
        String password = request.get("password");
        Optional<User> user = userService.loginUser(email, password);

        if (user.isPresent()) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            UserDetails userDetails = userService.loadUserByUsername(email);
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "email", email,
                    "id", String.valueOf(user.get().getId())));
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }

    /**
     * Handles user registration.
     *
     * @param request the registration request containing email and password
     * @return a response entity containing registration information
     */
    @Override
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        if (!request.containsKey("email") || !request.containsKey("password")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid request"));
        }
        String password = request.get("password");
        String email = request.get("email");

        try {
            userService.registerUser(password, email);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            UserDetails userDetails = userService.loadUserByUsername(email);
            User user = userRepository.findByEmail(email).orElse(null);
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            assert user != null;
            return ResponseEntity.ok(Map.of(
                    "message", "Registration successful",
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "email", email,
                    "id", String.valueOf(user.getId())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Creates a Stripe customer.
     *
     * @param name  the name of the customer
     * @param email the email of the customer
     * @return the created Stripe customer
     * @throws StripeException if an error occurs while creating the customer
     */
    @PostMapping("/stripe/customer")
    public Customer createCustomer(@RequestParam String name, @RequestParam String email) throws StripeException {
        return stripeService.createCustomer(name, email);
    }

    /**
     * Attaches a payment method to a Stripe customer.
     *
     * @param paymentMethodId the ID of the payment method
     * @param customerId      the ID of the customer
     * @return the attached payment method
     * @throws StripeException if an error occurs while attaching the payment method
     */
    @PostMapping("/stripe/payment-method")
    public PaymentMethod attachPaymentMethodToCustomer(@RequestParam String paymentMethodId, @RequestParam String customerId) throws StripeException {
        return stripeService.attachPaymentMethodToCustomer(paymentMethodId, customerId);
    }

    /**
     * Creates a Stripe product.
     *
     * @param productName        the name of the product
     * @param productDescription the description of the product
     * @return the created Stripe product
     * @throws StripeException if an error occurs while creating the product
     */
    @PostMapping("/stripe/product")
    public Product createProduct(@RequestParam String productName, @RequestParam String productDescription) throws StripeException {
        return stripeService.createProduct(productName, productDescription);
    }

    /**
     * Refreshes the JWT token.
     *
     * @param refreshRequest the refresh token request
     * @return a response entity containing the new access and refresh tokens
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto refreshRequest) {
        try {
            if (!jwtUtil.isRefreshToken(refreshRequest.getRefreshToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String username = jwtUtil.extractUsername(refreshRequest.getRefreshToken());
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (!jwtUtil.validateToken(refreshRequest.getRefreshToken(), userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("accessToken", newAccessToken);
            responseBody.put("refreshToken", newRefreshToken);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Deletes the user account.
     *
     * @param authHeader the authorization header containing the JWT token
     * @return a response entity containing the deletion status
     */
    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteAccount(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(email);
            if (!jwtUtil.validateToken(token, userDetails)) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
            }
            userService.deleteUser(email);
            return ResponseEntity.ok(Map.of("message", "Account successfully deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Updates the user profile.
     *
     * @param authHeader the authorization header containing the JWT token
     * @param updates    the updates to apply to the profile
     * @return a response entity containing the update status
     */
    @Override
    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> updates
    ) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(email);
            if (!jwtUtil.validateToken(token, userDetails)) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
            }
            userService.updateProfile(email, updates);
            return ResponseEntity.ok(Map.of("message", "Profile successfully changed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Logs out the user.
     *
     * @param request the HTTP request
     * @return a response entity containing the logout status
     */
    @Override
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        try {
            request.getSession().invalidate();
            return ResponseEntity.ok(Map.of("message", "Successfully logged out"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error during logout"));
        }
    }

    /**
     * Changes the user password.
     *
     * @param authHeader the authorization header containing the JWT token
     * @param request    the password change request
     * @return a response entity containing the change status
     */
    @Override
    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PasswordChangeRequest request
    ) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(email);
            if (!jwtUtil.validateToken(token, userDetails)) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
            }
            userService.changePassword(email, request.currentPassword(), request.newPassword());
            return ResponseEntity.ok(Map.of("message", "Password successfully changed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Retrieves the user profile.
     *
     * @param authHeader the authorization header containing the JWT token
     * @return a response entity containing the user profile
     */
    @Override
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            User user = userService.getUserProfile(email);
            String role = user.getRole().toString();

            return ResponseEntity.ok(Map.of(
                    "name", user.getBio() != null ? Objects.requireNonNull(user.getName()) : "",
                    "email", user.getEmail() != null ? user.getEmail() : "",
                    "bio", user.getBio() != null ? user.getBio() : "",
                    "role", role
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Searches for users based on a query.
     *
     * @param query the search query
     * @return a response entity containing the list of users
     */
    @Override
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
        try {
            List<UserDto> users = userService.searchUsers(query);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}



