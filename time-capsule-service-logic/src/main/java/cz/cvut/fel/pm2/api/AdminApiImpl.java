package cz.cvut.fel.pm2.api;

import cz.cvut.fel.pm2.model.UserDto;
import cz.cvut.fel.pm2.service.CapsuleService;
import cz.cvut.fel.pm2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminApiImpl implements AdminApi {
    private final UserService userService;
    private final CapsuleService capsuleService;

    @Override
    public ResponseEntity<List<String>> findEmails(String query) {
        return ResponseEntity.ok(userService.findEmails(query));
    }

    @Override
    public ResponseEntity<UserDto> getUserByEmail(String email) {
        return ResponseEntity.ok(userService.getAdminUser(email));
    }

    @Override
    public ResponseEntity<Boolean> updateUser(UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(userDto));
    }

    @Override
    public ResponseEntity<Boolean> deleteCapsule(Long capsuleId) {
        return ResponseEntity.ok(capsuleService.deleteCapsule(capsuleId));
    }

}
