package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.service.CapsuleService;
import cz.cvut.fel.pm2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for admin-related API endpoints.
 */
@RestController
@RequiredArgsConstructor
public class AdminApiImpl implements AdminApi {
    private final UserService userService;
    private final CapsuleService capsuleService;

    /**
     * Finds emails based on a query.
     *
     * @param query the search query
     * @return a list of emails matching the query
     */
    @Override
    public ResponseEntity<List<String>> findEmails(String query) {
        return ResponseEntity.ok(userService.findEmails(query));
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user
     * @return the user data transfer object
     */
    @Override
    public ResponseEntity<UserDto> getUserByEmail(String email) {
        return ResponseEntity.ok(userService.getAdminUser(email));
    }

    /**
     * Updates a user.
     *
     * @param userDto a map containing user details to update
     * @return true if the update was successful, false otherwise
     */
    @Override
    public ResponseEntity<Boolean> updateUser(Map<String, String> userDto) {
        return ResponseEntity.ok(userService.updateUser(userDto));
    }

    /**
     * Deletes a capsule.
     *
     * @param capsuleId the ID of the capsule to delete
     * @return true if the deletion was successful, false otherwise
     */
    @Override
    public ResponseEntity<Boolean> deleteCapsule(Long capsuleId) {
        return ResponseEntity.ok(capsuleService.deleteCapsule(capsuleId));
    }
}
