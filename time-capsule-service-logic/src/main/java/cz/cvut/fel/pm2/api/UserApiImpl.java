package cz.cvut.fel.pm2.api;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Product;
import cz.cvut.fel.pm2.config.security.JwtUtil;
import cz.cvut.fel.pm2.model.RefreshTokenRequestDto;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.UserRepository;
import cz.cvut.fel.pm2.service.CapsuleService;
import cz.cvut.fel.pm2.service.StripeService;
import cz.cvut.fel.pm2.service.UserService;
import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserApiImpl implements UserApi {
    private UserRepository userRepository;
    private CapsuleRepository capsuleRepository;

    private final UserService userService;
    private final CapsuleService capsuleService;
    private final StripeService stripeService;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    @Override
    @PostMapping(value = "/login/sso",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody()
    public ResponseEntity<Map<String, String>> login(@AuthenticationPrincipal(errorOnInvalidType=true) Object principal ) {
        Map<String, String> response = new HashMap<>();

        if (principal instanceof OidcUser oidcUser) {
            response.put("id", oidcUser.getSubject());
            response.put("name", oidcUser.getFullName());
            response.put("email", oidcUser.getEmail());
            response.put("message", "Login successful via SSO");
        } else if (principal instanceof UserDetails userDetails) {
            response.put("email", userDetails.getUsername());
            response.put("message", "Login successful with standard credentials");
        } else {
            response.put("message", "User not authenticated");
        }

        // Ensure you are returning a valid JSON body, not empty
//        return ResponseEntity.ok()
//                .header("Content-Type", "application/json")
//                .body(response); // This makes sure the body is actually returned

        return new ResponseEntity<>(response, HttpStatus.OK);
//        return response;
    }

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


    @PostMapping("/register/sso")
    public Map<String, Object> register(@AuthenticationPrincipal OidcUser oidcUser) {
        Map<String, Object> response = new HashMap<>();
        if (oidcUser != null) {
            userService.findOrCreateUser(oidcUser);
            response.put("message", "Registration successful");
        } else {
            response.put("message", "User not authenticated");
        }
        return response;
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        Optional<User> user = userService.loginUser(email, password);


        if (user.isPresent()) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            final UserDetails userDetails = userService.loadUserByUsername(email);
            final String accessToken = jwtUtil.generateToken(userDetails);
            final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "accessToken", accessToken,
                    "refreshToken", refreshToken));
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        if (!request.containsKey("email") || !request.containsKey("password")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid request"));
        }
        String password = request.get("password");
        String email = request.get("email");

        User user = userRepository.findByEmail(email).orElse(null);
        user.setPassword(password);
        try {
//            userService.registerUser(password, email);
//            User user = userService.getUser(email);
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            final UserDetails userDetails = userService.loadUserByUsername(email);

            final String accessToken = jwtUtil.generateToken(userDetails);
            final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            return ResponseEntity.ok(Map.of(
                    "message", "Registration successful",
                    "accessToken", accessToken,
                    "refreshToken", refreshToken,
                    "id", String.valueOf(user.getId())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/stripe/customer")
    public Customer createCustomer(@RequestParam String name, @RequestParam String email) throws StripeException {
        return stripeService.createCustomer(name, email);
    }

    @PostMapping("/stripe/payment-method")
    public PaymentMethod attachPaymentMethodToCustomer(@RequestParam String paymentMethodId, @RequestParam String customerId) throws StripeException {
        return stripeService.attachPaymentMethodToCustomer(paymentMethodId, customerId);
    }

    @PostMapping("/stripe/product")
    public Product createProduct(@RequestParam String productName, @RequestParam String productDescription) throws StripeException {
        return stripeService.createProduct(productName, productDescription);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto refreshRequest) {

        System.out.println("refresh token");
        try {
            if (!jwtUtil.isRefreshToken(refreshRequest.getRefreshToken())) {
                System.out.println("not refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            final String username = jwtUtil.extractUsername(refreshRequest.getRefreshToken());
            if (username == null) {
                System.out.println("no username");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            final UserDetails userDetails = userService.loadUserByUsername(username);

            if (!jwtUtil.validateToken(refreshRequest.getRefreshToken(), userDetails)) {
                System.out.println("not valid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            final String newAccessToken = jwtUtil.generateToken(userDetails);
            final String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("accessToken", newAccessToken);
            responseBody.put("refreshToken", newRefreshToken);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
