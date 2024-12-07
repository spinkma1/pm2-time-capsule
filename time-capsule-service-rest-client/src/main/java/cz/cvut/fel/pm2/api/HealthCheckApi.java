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

@Tag(name = "HealtCheck API", description = "API for user authentication and profile information.")
public interface HealthCheckApi {

    @Operation(summary = "Create a new Stripe product", description = "Creates a new product in Stripe.")
    @GetMapping("/")
    @ResponseBody
    Product createProduct(@RequestParam String productName, @RequestParam String productDescription) throws StripeException;
}
