package cz.cvut.fel.pm2;

import cz.cvut.fel.pm2.model.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Tag(name = "Admin API", description = "API for getting admin/privileged information in the application.")
@RequestMapping("/admin")
public interface AdminApi {
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/findEmails/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<String>> findEmails(@PathVariable String query);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/getUserByEmail/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> getUserByEmail(@PathVariable String email);

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/updateUser", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> updateUser(@RequestParam UserDto userDto);

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/deleteCapsule/{capsuleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> deleteCapsule(@PathVariable Long capsuleId);
}
