    package cz.cvut.fel.pm2.config.security;


    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.JsonMappingException;
    import com.nimbusds.jose.JWSAlgorithm;
    import com.nimbusds.jose.jwk.JWKSet;
    import com.nimbusds.jose.jwk.RSAKey;
    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.JwtException;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.UnsupportedJwtException;
    import io.jsonwebtoken.security.Keys;
    import jakarta.annotation.PostConstruct;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Component;
    import org.springframework.web.client.RestTemplate;

    import java.nio.charset.StandardCharsets;
    import java.security.Key;
    import java.security.PublicKey;
    import java.util.*;
    import java.util.function.Function;
    import java.util.stream.Collectors;
    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    @Component
    public class JwtUtil {
        @Value("${jwt.secret}")
        private String secretString;

        // URL to the public key set (JWKS) from Google or any OAuth2 provider
        private static final String GOOGLE_CERTS_URL = "https://www.googleapis.com/oauth2/v3/certs";



        private static Map<String, PublicKey> cachedPublicKeys;

private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtUtil.class);

        private Key key;

        @PostConstruct
        public void init() {
            byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }

        private static final long JWT_TOKEN_VALIDITY = 24L * 60 * 60 * 1000 * 365; // year
        private static final long REFRESH_TOKEN_VALIDITY = 30L * 24 * 60 * 60 * 1000; // 30 days

        private static final long KEY_CACHE_EXPIRY_TIME_MS = 60 * 60 * 1000; // 1 hour
        private static long lastFetchTime = 0;

        private static Map<String, PublicKey> getPublicKeys() {
            long currentTime = System.currentTimeMillis();
            // Check if cache is expired
            if (cachedPublicKeys == null || (currentTime - lastFetchTime) > KEY_CACHE_EXPIRY_TIME_MS) {
                cachedPublicKeys = downloadGooglePublicKeys();
                lastFetchTime = currentTime;  // Update the last fetch time
            }
            return cachedPublicKeys;
        }


        // Download public keys from Google (or your OAuth2 provider)
        private static Map<String, PublicKey> downloadGooglePublicKeys() {
            RestTemplate restTemplate = new RestTemplate();
            try {
                String certs = restTemplate.getForObject(GOOGLE_CERTS_URL, String.class);
                // Parse the certificates from JSON, and convert them to PublicKey objects
                return parsePublicKeys(certs);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download public keys from Google", e);
            }
        }



        private static Map<String, PublicKey> parsePublicKeys(String certs) {
            try {
                // Parse the JWKS (JSON Web Key Set)
                JWKSet jwkSet = JWKSet.parse(certs);

                // This will store the parsed public keys (keyId -> PublicKey)
                Map<String, PublicKey> publicKeys = new HashMap<>();

                // Iterate through each JWK in the set
                List<RSAKey> rsaKeys = jwkSet.getKeys().stream()
                        .filter(jwk -> jwk.getAlgorithm() != null && jwk.getAlgorithm().equals(JWSAlgorithm.RS256))
                        .map(jwk -> (RSAKey) jwk)
                        .collect(Collectors.toList());

                // For each RSA key, extract the PublicKey and map it by its 'kid' (key ID)
                for (RSAKey rsaKey : rsaKeys) {
                    PublicKey publicKey = rsaKey.toPublicKey(); // Convert RSAKey to PublicKey
                    publicKeys.put(rsaKey.getKeyID(), publicKey);
                }

                return publicKeys;
            } catch (Exception e) {
                throw new RuntimeException("Error parsing JWKS", e);
            }
        }
        public String extractUsername(String token) {
            try {
                // Split the JWT token
                String[] parts = token.split("\\.");

                // Ensure it's a valid token with 3 parts (header, payload, signature)
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid JWT token structure");
                }

                // Decode the payload part (index 1)
                String payload = parts[1];
                Base64.Decoder decoder = Base64.getUrlDecoder();
                String decodedPayload = new String(decoder.decode(payload), StandardCharsets.UTF_8);

                // Parse the payload as JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode payloadJson = objectMapper.readTree(decodedPayload);

                // Try to extract the email, but fall back to 'sub' or 'username' in non-Google tokens
                String email = null;

                // For Google tokens, "email" is the common field.
                if (payloadJson.has("email")) {
                    email = payloadJson.get("email").asText();
                }

                // Fall back to "sub" (subject), which is typically the user ID, for non-Google tokens
                if (email == null && payloadJson.has("sub")) {
                    email = payloadJson.get("sub").asText();  // 'sub' is commonly used in standard JWTs as a user identifier.
                }

                // Additionally, we could fall back to "username" if the token has that field (custom setup)
                if (email == null && payloadJson.has("username")) {
                    email = payloadJson.get("username").asText();
                }

                if (email == null) {
                    throw new IllegalArgumentException("Unable to extract username/email from token");
                }

                return email;  // Return the email or username found in the JWT payload
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract username (email) from JWT", e);
            }
        }


        public Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }


        public  String getAlgorithm(String token)
        {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token structure");
            }

            String header = parts[0];
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String decodedHeader = new String(decoder.decode(header), StandardCharsets.UTF_8);

            // Use Jackson's ObjectMapper to parse the header JSON
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode headerJson = objectMapper.readTree(decodedHeader);
                return headerJson.get("alg").asText();  // Retrieve 'alg' as text
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract 'alg' from JWT header", e);
            }
        }

        private Claims extractAllClaims(String token) {
            try {
                // JWT structure: header.payload.signature
                String[] parts = token.split("\\.");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid JWT token structure");
                }

                // Decode the header part of JWT to get "alg" and "kid"
                String header = parts[0];
                Base64.Decoder decoder = Base64.getUrlDecoder();
                String decodedHeader = new String(decoder.decode(header), StandardCharsets.UTF_8);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode headerJson = objectMapper.readTree(decodedHeader);

                // Extract 'alg' (signing algorithm) and 'kid' (key identifier) from JWT header
                String algorithm = headerJson.get("alg").asText(); // "HS256", "RS256", etc.

                // Extract 'kid' (key ID) from JWT header IF using RSA
                String kid = null;
                if (algorithm.equals("RS256")) {
                    kid = headerJson.get("kid").asText(); // The key ID used for RSA
                }

                Key signingKey;

                // Determine the correct signing key based on the algorithm
                switch (algorithm) {
                    case "HS256":  // HMAC with SHA-256
                        signingKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
                        break;

                    case "RS256":  // RSA with SHA-256
                        // Fetch the public key dynamically using the 'kid'
                        signingKey = getPublicKeys().get(kid);  // Get the public key using 'kid'

                        if (signingKey == null) {
                            throw new UnsupportedJwtException("Public key not found for kid: " + kid);
                        }
                        break;

                    default:
                        throw new UnsupportedJwtException("Unsupported signing algorithm: " + algorithm);
                }

                // After determining the signing key, parse and validate JWT claims
                return Jwts.parserBuilder()
                        .setSigningKey(signingKey)  // Set the correct key for verification
                        .build()
                        .parseClaimsJws(token)  // Use parseClaimsJws() to validate the token
                        .getBody(); // Get the claims body

            } catch (JwtException e) {
                logger.error("Failed to parse JWT: ", e);
                throw new IllegalArgumentException("Invalid JWT token", e);
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public String extractEmailFromJwt(String token) {
            // Split the JWT by dots
            String[] parts = token.split("\\.");

            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token structure");
            }

            // Extract and decode the payload (second part of the JWT)
            String payload = parts[1];

            // Decode the Base64 URL encoded string
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String decodedPayload = new String(decoder.decode(payload), StandardCharsets.UTF_8);

            // Parse the decoded payload JSON to extract the email
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode payloadJson = objectMapper.readTree(decodedPayload);

                // Look for the "email" claim (this is typically available in Google JWTs)
                if (payloadJson.has("email")) {
                    return payloadJson.get("email").asText();
                } else {
                    throw new IllegalArgumentException("Email not found in the token payload.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse JWT payload", e);
            }
        }

        private Boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

        public String generateToken(UserDetails userDetails) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("type", "access");
            return createToken(claims, userDetails.getUsername(), JWT_TOKEN_VALIDITY);
        }

        private String createToken(Map<String, Object> claims, String subject, long validity) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + validity))
                    .signWith(key)
                    .compact();
        }

        public Boolean validateToken(String token, UserDetails userDetails) {
            final String username = extractUsername(token);

            boolean isTokenExpired = isTokenExpired(token);
            boolean isUsernameValid = username.equals(userDetails.getUsername());
            return isUsernameValid && !isTokenExpired;
        }
        public boolean validateTokenRS256(String token, UserDetails userDetails) {
                final String username = extractUsername(token);

                // Extract 'kid' from JWT header
                String kid = extractKidFromJwtHeader(token); // Extract kid using the previous step

                // Fetch the corresponding public key
                PublicKey publicKey = getPublicKeys().get(kid); // Get the public key based on 'kid'

                if (publicKey != null) {
                    // Validate the JWT signature with the fetched public key
                    Jwts.parserBuilder()
                            .setSigningKey(publicKey)  // Use the correct public key
                            .build()
                            .parseClaimsJws(token);
                }

                return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            }

        private String extractKidFromJwtHeader(String token) {
            String[] parts = token.split("\\."); // JWT structure: header.payload.signature
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token structure");
            }

            String header = parts[0];
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String decodedHeader = new String(decoder.decode(header), StandardCharsets.UTF_8);

            // Use Jackson's ObjectMapper to parse the header JSON
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode headerJson = objectMapper.readTree(decodedHeader);
                return headerJson.get("kid").asText();  // Retrieve 'kid' as text
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract 'kid' from JWT header", e);
            }
        }

        public String generateRefreshToken(UserDetails userDetails) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("type", "refresh");
            return createToken(claims, userDetails.getUsername(), REFRESH_TOKEN_VALIDITY);
        }

        public boolean isRefreshToken(String token) {
            final Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("type"));
        }
    }