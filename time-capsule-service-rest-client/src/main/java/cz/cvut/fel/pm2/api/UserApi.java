package cz.cvut.fel.pm2.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Tag(name = "User API", description = "API for user authentication and profile information.")
@RequestMapping("/user")
public interface UserApi {

    @Operation(summary = "Login with Google SSO", description = "Initiates login process with Google Single Sign-On.")
    @GetMapping("/login")
    @ResponseBody
    Map<String, String> login(OidcUser oidcUser);

    @Operation(summary = "Get User Information", description = "Returns user information including capsules and followers.")
    @GetMapping("/info")
    @ResponseBody
    Map<String, Object> getUserInfo(OidcUser oidcUser);
}
