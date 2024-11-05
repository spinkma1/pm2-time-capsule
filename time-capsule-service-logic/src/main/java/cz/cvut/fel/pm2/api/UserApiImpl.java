package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserApiImpl implements UserApi {
    private final UserService userService;

    @Override
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
}
