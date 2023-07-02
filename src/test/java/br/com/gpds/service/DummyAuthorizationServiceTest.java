package br.com.gpds.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static br.com.gpds.security.SecurityUtils.JWT_ALGORITHM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class DummyAuthorizationServiceTest {

    private DummyAuthorizationService dummyAuthorizationService;
    private String jwtKey;
    private JwtEncoder jwtEncoder;

    @BeforeEach
    void setUp() {
        jwtKey = """
            NTRjZmMxYzhlZDEzNjMwNmI0NGI4MzQ0N2VhYjM4YmVjYTdjYWNkNTA0OTViODY0Y2M2OTZiMzFiNzI3YmE4MGM1ZDZkMTdkYmVmOGIxNjcxODllMDRiZjgxYWMyMGQ5MjM5MTc1ZTBlNjFiYTJlMTVjMDRlYzhlOTAzNmMyNzU=
            """;
        jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
        dummyAuthorizationService = new DummyAuthorizationService(jwtEncoder);
        dummyAuthorizationService.setTokenValidityInMilliseconds(86400L);
        dummyAuthorizationService.setTokenValidityInMillisecondsForRememberMe(2592000L);
    }

    SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    @Test
    @DisplayName("Should token validity in milliseconds be expired in a day is true")
    void getTokenValidityInMillisecondsExpiresInADay() {
        var now = Instant.now();
        var millis = 1000 * dummyAuthorizationService.getTokenValidityInMilliseconds();
        var expireIn = now.plusMillis(millis);

        var expireTimeInMillis = expireIn.minus(now.toEpochMilli(), ChronoUnit.MILLIS);
        assertEquals(Instant.ofEpochMilli(millis), Instant.ofEpochMilli(expireTimeInMillis.toEpochMilli()));
        assertEquals(Instant.now().plusMillis(expireTimeInMillis.toEpochMilli()), Instant.now().plus(1, ChronoUnit.DAYS));
    }

    @Test
    @DisplayName("Should token validity in milliseconds for remember me be expired in a month is true")
    void getTokenValidityInMillisecondsExpiresForRememberMeInAMonth() {
        var now = Instant.now();
        var millis = 1000 * dummyAuthorizationService.getTokenValidityInMillisecondsForRememberMe();
        var expireIn = now.plusMillis(millis);

        var expireTimeInMillis = expireIn.minus(now.toEpochMilli(), ChronoUnit.MILLIS);
        assertEquals(Instant.ofEpochMilli(millis), Instant.ofEpochMilli(expireTimeInMillis.toEpochMilli()));
        assertEquals(
            Instant.now().plusMillis(expireTimeInMillis.toEpochMilli()),
            Instant.now().plus(30, ChronoUnit.DAYS)
        );
    }

    @Test
    @DisplayName("Should return JWT with regular validity when rememberMe is false")
    void getJwtForDummyUserWhenRememberMeIsFalse() {
        var actualJwtResponse = dummyAuthorizationService.getJwtForDummyUser(false);
        assertFalse(actualJwtResponse.token().isEmpty());
    }

    @Test
    @DisplayName("Should return JWT with validity for remember me when rememberMe is true")
    void getJwtForDummyUserWhenRememberMeIsTrue() {
        var actualJwtResponse = dummyAuthorizationService.getJwtForDummyUser(true);
        assertFalse(actualJwtResponse.token().isEmpty());
    }

}
