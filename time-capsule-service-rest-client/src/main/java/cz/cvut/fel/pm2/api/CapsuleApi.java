package cz.cvut.fel.pm2.api;
import cz.cvut.fel.pm2.api.examples.ExampleStrings;
import cz.cvut.fel.pm2.exceptions.InvalidBodyException;
import cz.cvut.fel.pm2.exceptions.NotFoundException;
import cz.cvut.fel.pm2.model.CapsuleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
/**
 * API interface for discount-related operations.
 */
@Tag(name = "Capsule API", description = "API for time capsules.")
@RequestMapping("/capsules")
public interface CapsuleApi {
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "basicAuth")
    @Operation(
            summary =
                    "Updates capsule parameters.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "210",
                            description = "CREATED"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Specified capsule not found",
                            content =
                            @Content(examples = {
                                    @ExampleObject(name = "Specified capsule not found",
                                            summary = "Specified capsule not found",
                                            description = "Specified capsule not found.",
                                            value = ExampleStrings.RESPONSE_CAPSULES_404_EXAMPLE)},
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = NotFoundException.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Body not sent, or wrong body.",
                            content =
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = InvalidBodyException.class))),
            })
    ResponseEntity<CapsuleDto> createCapsule(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Capsule information",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CapsuleDto.class,
                                    example = """
                                    {
                                      "id": "1",
                                      "name": "SUMMER2023",
                                      "description": "Summer Capsule",
                                      "startDate": "2023-06-01",
                                      "expirationDate": "2023-09-01"
                                    }
                                    """),
                            examples = {
                                    @ExampleObject(name = "Capsule information",
                                            summary = "Capsule information body",
                                            description = "Capsule information body.",
                                            value = """
                                    {
                                      "id": "1",
                                      "name": "SUMMER2023",
                                      "description": "Summer Capsule",
                                      "startDate": "2023-06-01",
                                      "expirationDate": "2023-09-01"
                                    }
                                    """)}
                    )
            )
            @RequestBody CapsuleDto capsule) throws NoSuchAlgorithmException;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<CapsuleDto>> getCapsules(@RequestParam String email);


    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{capsuleId}")
    @ResponseBody
    ResponseEntity<CapsuleDto> readyCapsule(@PathVariable String capsuleId, @RequestParam boolean ready);

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/openviaqr")
    @ResponseBody
    ResponseEntity<Map<String, String>> openViaQr(@RequestParam String capsuleId, @RequestParam String qrCode) throws NoSuchAlgorithmException;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{capsuleId}")
    @ResponseBody
    ResponseEntity<CapsuleDto> getCapsuleDetails(@PathVariable String capsuleId);

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{capsuleId}/subscribe/{userId}")
    @ResponseBody
    ResponseEntity<CapsuleDto> subscribeToCapsule(@PathVariable String capsuleId, @PathVariable String userId);

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{capsuleId}/unlock-early")
    @ResponseBody
    ResponseEntity<CapsuleDto> unlockCapsuleEarly(@PathVariable String capsuleId);
}
