package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.config.security.JwtUtil;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.persistence.User;
import cz.cvut.fel.pm2.service.FollowService;
import cz.cvut.fel.pm2.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FollowApiImpl implements FollowApi {

    private final FollowService followService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<Void> followUser(@RequestHeader("Authorization") String authHeader, @PathVariable Long followedId) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            User user = userService.getUserProfile(email);
            Long followerId = user.getId();

            if (followedId == null) {
                return ResponseEntity.badRequest().build();
            }

            followService.followUser(followedId, followerId);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Void> unfollowUser(@RequestHeader("Authorization") String authHeader, @PathVariable Long followedId) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            User user = userService.getUserProfile(email);
            Long followerId = user.getId();

            if (followedId == null) {
                return ResponseEntity.badRequest().build();
            }

            followService.unfollowUser(followedId, followerId);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<List<UserDto>> getFollowers() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            List<UserDto> followers = followService.getFollowers(email);
            return ResponseEntity.ok(followers);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<List<UserDto>> getFollowing() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            List<UserDto> following = followService.getFollowing(email);
            return ResponseEntity.ok(following);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}