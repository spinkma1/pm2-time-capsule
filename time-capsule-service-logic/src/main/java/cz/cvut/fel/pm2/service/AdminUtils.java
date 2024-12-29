package cz.cvut.fel.pm2.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused") // used in @PreAuthorize
public class AdminUtils {
    /**
     * checks if the logged-in user has admin rights.
     *
     * @return boolean true if the user has admin rights.
     */
    @SuppressWarnings("unused") // used in @PreAuthorize
    public boolean checkForAdminRights() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
