package cz.cvut.fel.pm2.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for health check API endpoints.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * Endpoint for checking the health status of the application.
     *
     * @return a ResponseEntity containing the health status and current timestamp
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", new Date());
        return ResponseEntity.ok(response);
    }
}
