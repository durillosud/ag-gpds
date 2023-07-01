package br.com.gpds.security.jwt;

import br.com.gpds.management.SecurityMetersService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JWTFilterTest {

    private JWTFilter jwtFilter;
    private JwtDecoder jwtDecoder;
    private SecurityMetersService securityMetersService;
    private JwtAuthenticationConverter jwtAuthenticationConverter;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
//        meterRegistry = mock(MeterRegistry.class);
        securityMetersService = mock(SecurityMetersService.class);
        jwtDecoder = mock(JwtDecoder.class);
        jwtAuthenticationConverter = mock(JwtAuthenticationConverter.class);
        jwtFilter = new JWTFilter(jwtDecoder, securityMetersService, jwtAuthenticationConverter);
    }


    @Test
    @DisplayName("Should throw an exception when the token is unsecured/JWS/JWE")
    void validateTokenWhenTokenIsUnsecuredJwsJweThenThrowException() {
        String token = "invalidToken";

        when(jwtDecoder.decode(token)).thenThrow(new RuntimeException("Invalid unsecured/JWS/JWE"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(token));
        verify(securityMetersService, times(1)).trackTokenMalformed();
    }

    @Test
    @DisplayName("Should throw an exception when the token has an invalid signature")
    void validateTokenWhenSignatureIsInvalidThenThrowException() {
        String invalidToken = "invalidToken";

        when(jwtDecoder.decode(invalidToken)).thenThrow(new RuntimeException("Invalid signature"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(invalidToken));
        verify(securityMetersService, times(1)).trackTokenInvalidSignature();
    }

    @Test
    @DisplayName("Should throw an exception when the token is expired")
    void validateTokenWhenTokenIsExpiredThenThrowException() {
        String expiredToken = "expiredToken";

        when(jwtDecoder.decode(expiredToken)).thenThrow(new JwtException("Token expired"));

        assertThrows(JwtException.class, () -> jwtFilter.validateToken(expiredToken));
    }

    @Test
    @DisplayName("Should validate the token when it is valid")
    void validateTokenWhenItIsValid() {
        String validToken = "validToken";

        when(jwtDecoder.decode(validToken)).thenReturn(mock(Jwt.class));

        assertDoesNotThrow(() -> jwtFilter.validateToken(validToken));

        verify(securityMetersService, never()).trackTokenInvalidSignature();
        verify(securityMetersService, never()).trackTokenExpired();
        verify(securityMetersService, never()).trackTokenMalformed();
    }

    @Test
    @DisplayName("Should throw an exception when the token is malformed")
    void validateTokenWhenTokenIsMalformedThenThrowException() {
        String malformedToken = "invalid_token";

        when(jwtDecoder.decode(malformedToken)).thenThrow(new RuntimeException("Invalid JWT serialization"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(malformedToken));
        verify(securityMetersService, times(1)).trackTokenMalformed();
    }

    @Test
    @DisplayName("Should return false when the HTTP method is not OPTIONS")
    void checkedHttpOptionsHeaderFromRequestWhenMethodIsNotOptions() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getMethod()).thenReturn("GET");

        boolean result = jwtFilter.checkedHttpOptionsHeaderFromRequest(httpServletRequest);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when the HTTP method is OPTIONS and the request headers contain either SESSION_KEY or AUTHORIZATION")
    void checkedHttpOptionsHeaderFromRequestWhenMethodIsOptionsAndHeadersContainSessionKeyOrAuthorization() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getMethod()).thenReturn("OPTIONS");

        String sessionKeyHeader = "X-SessionKey";
        String authorizationHeader = "Authorization";

        when(httpServletRequest.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS))
            .thenReturn(sessionKeyHeader + ", " + authorizationHeader);

        boolean result = jwtFilter.checkedHttpOptionsHeaderFromRequest(httpServletRequest);

        assertTrue(result);
        verify(httpServletRequest, times(1)).getMethod();
        verify(httpServletRequest, times(1)).getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
    }

    @Test
    @DisplayName("Should return null when the Authorization header does not start with 'Bearer '")
    void resolveTokenWhenAuthorizationHeaderDoesNotStartWithBearer() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Token abc123");

        String token = ReflectionTestUtils.invokeMethod(jwtFilter, "resolveToken", request);

        assertNull(token);
    }

    @Test
    @DisplayName("Should return the token when the Authorization header starts with 'Bearer ' and is not empty")
    void resolveTokenWhenAuthorizationHeaderStartsWithBearerAndIsNotEmpty() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer token");

        String token = (String) ReflectionTestUtils.invokeMethod(jwtFilter, "resolveToken", request);

        assertEquals("token", token);
    }

    @Test
    @DisplayName("Should track invalid signature and throw exception when the token has invalid signature")
    void validateTokenWhenTokenHasInvalidSignatureThenTrackAndThrowException() {
        String invalidToken = "invalidToken";

        when(jwtDecoder.decode(invalidToken)).thenThrow(new RuntimeException("Invalid signature"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(invalidToken));

        verify(securityMetersService, times(1)).trackTokenInvalidSignature();
    }

    @Test
    @DisplayName("Should track token malformed and throw exception when the token is unsecured/JWS/JWE")
    void validateTokenWhenTokenIsUnsecuredJwsJweThenTrackAndThrowException() {
        String token = "invalid_token";

        when(jwtDecoder.decode(token)).thenThrow(new RuntimeException("Invalid unsecured/JWS/JWE"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(token));
        verify(securityMetersService, times(1)).trackTokenMalformed();
    }

    @Test
    @DisplayName("Should track token expired and throw exception when the token is expired")
    void validateTokenWhenTokenIsExpiredThenTrackAndThrowException() {
        String expiredToken = "expired_token";

        when(jwtDecoder.decode(expiredToken)).thenThrow(new RuntimeException("Jwt expired at"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(expiredToken));

        verify(securityMetersService, times(1)).trackTokenExpired();
    }

    @Test
    @DisplayName("Should track token malformed and throw exception when the token is malformed")
    void validateTokenWhenTokenIsMalformedThenTrackAndThrowException() {
        String malformedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        when(jwtDecoder.decode(malformedToken)).thenThrow(new RuntimeException("Invalid JWT serialization"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(malformedToken));
        verify(securityMetersService, times(1)).trackTokenMalformed();
    }
}
