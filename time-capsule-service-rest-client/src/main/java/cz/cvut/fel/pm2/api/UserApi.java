package cz.cvut.fel.pm2.api;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Product;
import cz.cvut.fel.pm2.model.PasswordChangeRequest;
import cz.cvut.fel.pm2.model.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "User API", description = "API for user authentication and profile information.")
@RequestMapping("/user")
public interface UserApi {

    /**
     * Initiates login process with Google Single Sign-On.
     *
     * @param principal the authenticated principal
     * @return a response entity containing login information
     */
    @Operation(summary = "Login with Google SSO", description = "Initiates login process with Google Single Sign-On.")
    @PostMapping("/login/sso")
    @ResponseBody
    ResponseEntity<Map<String, String>> login(Object principal);

    /**
     * Returns user information including capsules and followers.
     *
     * @param oidcUser the authenticated OIDC user
     * @return a map containing user information
     */
    @Operation(summary = "Get User Information", description = "Returns user information including capsules and followers.")
    @GetMapping("/info")
    @ResponseBody
    Map<String, Object> getUserInfo(OidcUser oidcUser);

    /**
     * Registers a new user using Google Single Sign-On.
     *
     * @param oidcUser the authenticated OIDC user
     * @return a response entity containing registration information
     */
    @Operation(summary = "Register a new user", description = "Registers a new user using Google Single Sign-On.")
    @PostMapping("/register/sso")
    @ResponseBody
    ResponseEntity<Map<String, Object>> register(@AuthenticationPrincipal OidcUser oidcUser);

    /**
     * Registers a new user using password and username.
     *
     * @param request the registration request containing email and password
     * @return a response entity containing registration information
     */
    @Operation(summary = "Register a new user", description = "Registers a new user using password and username.")
    @PostMapping("/register")
    @ResponseBody
    ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request);

    /**
     * Login an existing user using password and username.
     *
     * @param request the login request containing email and password
     * @return a response entity containing login information
     * @throws IllegalAccessException if the login fails
     */
    @Operation(summary = "Login an existing user", description = "Login an existing user using password and username.")
    @PostMapping("/login")
    @ResponseBody
    ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) throws IllegalAccessException;

    /**
     * Creates a new customer in Stripe.
     *
     * @param name the name of the customer
     * @param email the email of the customer
     * @return the created Stripe customer
     * @throws StripeException if an error occurs while creating the customer
     */
    @Operation(summary = "Create a new Stripe customer", description = "Creates a new customer in Stripe.")
    @PostMapping("/stripe/customer")
    @ResponseBody
    Customer createCustomer(@RequestParam String name, @RequestParam String email) throws StripeException;

    /**
     * Attaches a payment method to an existing Stripe customer.
     *
     * @param paymentMethodId the ID of the payment method
     * @param customerId the ID of the customer
     * @return the attached payment method
     * @throws StripeException if an error occurs while attaching the payment method
     */
    @Operation(summary = "Attach a payment method to a Stripe customer", description = "Attaches a payment method to an existing Stripe customer.")
    @PostMapping("/stripe/payment-method")
    @ResponseBody
    PaymentMethod attachPaymentMethodToCustomer(@RequestParam String paymentMethodId, @RequestParam String customerId) throws StripeException;

    /**
     * Creates a new product in Stripe.
     *
     * @param productName the name of the product
     * @param productDescription the description of the product
     * @return the created Stripe product
     * @throws StripeException if an error occurs while creating the product
     */
    @Operation(summary = "Create a new Stripe product", description = "Creates a new product in Stripe.")
    @PostMapping("/stripe/product")
    @ResponseBody
    Product createProduct(@RequestParam String productName, @RequestParam String productDescription) throws StripeException;

    /**
     * Permanently deletes user account and all associated data.
     *
     * @param authHeader the authorization header containing the JWT token
     * @return a response entity containing the deletion status
     */
    @DeleteMapping("/delete")
    @Operation(summary = "Delete user account", description = "Permanently deletes user account and all associated data")
    ResponseEntity<Map<String, String>> deleteAccount(@RequestHeader("Authorization") String authHeader);

    /**
     * Updates user profile information.
     *
     * @param authHeader the authorization header containing the JWT token
     * @param updates the updates to apply to the profile
     * @return a response entity containing the update status
     */
    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Updates user profile information")
    ResponseEntity<Map<String, String>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> updates
    );

    /**
     * Gets user profile information.
     *
     * @param authHeader the authorization header containing the JWT token
     * @return a response entity containing the user profile
     */
    @GetMapping("/profile")
    @Operation(summary = "Get user profile data", description = "Gets user profile information")
    ResponseEntity<Map<String, Object>> getUserProfile(@RequestHeader("Authorization") String authHeader);

    /**
     * Logs out the current user.
     *
     * @param request the HTTP request
     * @return a response entity containing the logout status
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logs out the current user")
    ResponseEntity<Map<String, String>> logout(HttpServletRequest request);

    /**
     * Changes user password.
     *
     * @param token the authorization token
     * @param request the password change request
     * @return a response entity containing the change status
     */
    @PutMapping("/password")
    @Operation(summary = "Change password", description = "Changes user password")
    ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody PasswordChangeRequest request
    );

    /**
     * Search users by email or name.
     *
     * @param query the search query
     * @return a response entity containing the list of users
     */
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by email or name")
    ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query);
}