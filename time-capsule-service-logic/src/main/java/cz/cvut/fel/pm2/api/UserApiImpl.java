package cz.cvut.fel.pm2.api;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Product;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.repository.CapsuleRepository;
import cz.cvut.fel.pm2.repository.UserRepository;
import cz.cvut.fel.pm2.service.CapsuleService;
import cz.cvut.fel.pm2.service.StripeService;
import cz.cvut.fel.pm2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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


    @Override
    @PostMapping("/login/sso")
    public Map<String, String> login(@AuthenticationPrincipal OidcUser oidcUser) {
        Map<String, String> response = new HashMap<>();
        if (oidcUser != null) {
            response.put("id", oidcUser.getSubject());
            response.put("name", oidcUser.getFullName());
            response.put("email", oidcUser.getEmail());
            response.put("message", "Login successful");
        } else {
            response.put("message", "User not authenticated");
        }
        return response;
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
            return ResponseEntity.ok(Map.of("message", "Login successful"));
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

        try {
            userService.registerUser(password, email);
            return ResponseEntity.ok(Map.of("message", "Registration successful"));
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
}
