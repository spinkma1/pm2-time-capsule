package cz.cvut.fel.pm2.api;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Tag(name = "User API", description = "API for user authentication and profile information.")
@RequestMapping("/user")
public interface UserApi {

    @Operation(summary = "Login with Google SSO", description = "Initiates login process with Google Single Sign-On.")
    @GetMapping("/login/sso")
    @ResponseBody
    Map<String, String> login(OidcUser oidcUser);

    @Operation(summary = "Get User Information", description = "Returns user information including capsules and followers.")
    @GetMapping("/info")
    @ResponseBody
    Map<String, Object> getUserInfo(OidcUser oidcUser);


    @Operation(summary = "Register a new user", description = "Registers a new user using Google Single Sign-On.")
    @PostMapping("/register/sso")
    @ResponseBody
    Map<String, Object> register(@AuthenticationPrincipal OidcUser oidcUser);

    @Operation(summary = "Register a new user", description = "Registers a new user using password and username.")
    @PostMapping("/register")
    @ResponseBody
    ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request);

    @Operation(summary = "Login an existing user", description = "Login an existing user using password and username.")
    @PostMapping("/login")
    @ResponseBody
    ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request);

    @Operation(summary = "Create a new Stripe customer", description = "Creates a new customer in Stripe.")
    @PostMapping("/stripe/customer")
    @ResponseBody
    Customer createCustomer(@RequestParam String name, @RequestParam String email) throws StripeException;

    @Operation(summary = "Attach a payment method to a Stripe customer", description = "Attaches a payment method to an existing Stripe customer.")
    @PostMapping("/stripe/payment-method")
    @ResponseBody
    PaymentMethod attachPaymentMethodToCustomer(@RequestParam String paymentMethodId, @RequestParam String customerId) throws StripeException;

    @Operation(summary = "Create a new Stripe product", description = "Creates a new product in Stripe.")
    @PostMapping("/stripe/product")
    @ResponseBody
    Product createProduct(@RequestParam String productName, @RequestParam String productDescription) throws StripeException;
}
