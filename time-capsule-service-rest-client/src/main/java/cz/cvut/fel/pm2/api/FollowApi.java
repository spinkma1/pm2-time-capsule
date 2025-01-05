package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.model.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Follow API", description = "API for managing user following relationships")
@RequestMapping("/follow")
public interface FollowApi {

    /**
     * Start following a specific user.
     *
     * @param authHeader the authorization header containing the JWT token
     * @param followedId the ID of the user to follow
     * @return a response entity with no content
     */
    @Operation(summary = "Follow a user", description = "Start following a specific user")
    @PostMapping("/{followedId}")
    ResponseEntity<Void> followUser(@RequestHeader("Authorization") String authHeader, @PathVariable Long followedId);

    /**
     * Stop following a specific user.
     *
     * @param authHeader the authorization header containing the JWT token
     * @param followedId the ID of the user to unfollow
     * @return a response entity with no content
     */
    @Operation(summary = "Unfollow a user", description = "Stop following a specific user")
    @DeleteMapping("/{followedId}")
    ResponseEntity<Void> unfollowUser(@RequestHeader("Authorization") String authHeader, @PathVariable Long followedId);

    /**
     * Get list of users following the current user.
     *
     * @return a response entity containing the list of followers
     */
    @Operation(summary = "Get followers", description = "Get list of users following the current user")
    @GetMapping("/followers")
    ResponseEntity<List<UserDto>> getFollowers();

    /**
     * Get list of users that the current user follows.
     *
     * @return a response entity containing the list of following users
     */
    @Operation(summary = "Get following", description = "Get list of users that the current user follows")
    @GetMapping("/following")
    ResponseEntity<List<UserDto>> getFollowing();
}