package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.model.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

@Tag(name = "Admin API", description = "API for getting admin/privileged information in the application.")
@RequestMapping("/admin")
public interface AdminApi {

    /**
     * Finds emails based on a query.
     *
     * @param query the search query
     * @return a list of emails matching the query
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/findEmails/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<String>> findEmails(@PathVariable String query);

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user
     * @return the user data transfer object
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/getUserByEmail/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> getUserByEmail(@PathVariable String email);

    /**
     * Updates a user.
     *
     * @param userDto a map containing user details to update
     * @return true if the update was successful, false otherwise
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/updateUser", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> updateUser(@RequestBody Map<String, String> userDto);

    /**
     * Deletes a capsule.
     *
     * @param capsuleId the ID of the capsule to delete
     * @return true if the deletion was successful, false otherwise
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/deleteCapsule/{capsuleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> deleteCapsule(@PathVariable Long capsuleId);
}