package cz.cvut.fel.pm2.carrentalservice.api.examples;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExampleStrings {
    /////////////////////// CapsuleAPI
    public static final String RESPONSE_CAPSULES_404_EXAMPLE = """
            {
                "timestamp": "2023-11-13T07:57:41.609+00:00",
                "status": 404,
                "error": "Not Found",
                "message": "Requested time capsule with name: 1234 could not be found",
                "path": "/capsules/1234"
            }
            """;
}
