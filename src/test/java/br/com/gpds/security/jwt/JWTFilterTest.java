package br.com.gpds.security.jwt;

import br.com.gpds.management.SecurityMetersService;
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

    @BeforeEach
    void setUp() {
        securityMetersService = mock(SecurityMetersService.class);
        jwtDecoder = mock(JwtDecoder.class);
        jwtAuthenticationConverter = mock(JwtAuthenticationConverter.class);
        jwtFilter = new JWTFilter(jwtDecoder, securityMetersService, jwtAuthenticationConverter);
    }


    @Test
    @DisplayName("Should throw an exception when the token is unsecured/JWS/JWE")
    void validateTokenWhenTokenIsUnsecuredJwsJweThenThrowException() {
        var token = "invalidToken";

        when(jwtDecoder.decode(token)).thenThrow(new RuntimeException("Invalid unsecured/JWS/JWE"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(token));
        verify(securityMetersService, times(1)).trackTokenMalformed();
    }

    @Test
    @DisplayName("Should throw an exception when the token has an invalid signature")
    void validateTokenWhenSignatureIsInvalidThenThrowException() {
        var invalidToken = "invalidToken";

        when(jwtDecoder.decode(invalidToken)).thenThrow(new RuntimeException("Invalid signature"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(invalidToken));
        verify(securityMetersService, times(1)).trackTokenInvalidSignature();
    }

    @Test
    @DisplayName("Should throw an exception when the token is expired")
    void validateTokenWhenTokenIsExpiredThenThrowException() {
        var expiredToken = "expiredToken";

        when(jwtDecoder.decode(expiredToken)).thenThrow(new JwtException("Token expired"));

        assertThrows(JwtException.class, () -> jwtFilter.validateToken(expiredToken));
    }

    @Test
    @DisplayName("Should validate the token when it is valid")
    void validateTokenWhenItIsValid() {
        var validToken = "validToken";

        when(jwtDecoder.decode(validToken)).thenReturn(mock(Jwt.class));

        assertDoesNotThrow(() -> jwtFilter.validateToken(validToken));

        verify(securityMetersService, never()).trackTokenInvalidSignature();
        verify(securityMetersService, never()).trackTokenExpired();
        verify(securityMetersService, never()).trackTokenMalformed();
    }

    @Test
    @DisplayName("Should throw an exception when the token is malformed")
    void validateTokenWhenTokenIsMalformedThenThrowException() {
        var malformedToken = "invalid_token";

        when(jwtDecoder.decode(malformedToken)).thenThrow(new RuntimeException("Invalid JWT serialization"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(malformedToken));
        verify(securityMetersService, times(1)).trackTokenMalformed();
    }

    @Test
    @DisplayName("Should return false when the HTTP method is not OPTIONS")
    void checkedHttpOptionsHeaderFromRequestWhenMethodIsNotOptions() {
        var httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getMethod()).thenReturn("GET");

        boolean result = jwtFilter.checkedHttpOptionsHeaderFromRequest(httpServletRequest);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when the HTTP method is OPTIONS and the request headers contain either SESSION_KEY or AUTHORIZATION")
    void checkedHttpOptionsHeaderFromRequestWhenMethodIsOptionsAndHeadersContainSessionKeyOrAuthorization() {
        var httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getMethod()).thenReturn("OPTIONS");

        var sessionKeyHeader = "X-SessionKey";
        var authorizationHeader = "Authorization";

        when(httpServletRequest.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS))
            .thenReturn(sessionKeyHeader + ", " + authorizationHeader);

        var result = jwtFilter.checkedHttpOptionsHeaderFromRequest(httpServletRequest);

        assertTrue(result);
        verify(httpServletRequest, times(1)).getMethod();
        verify(httpServletRequest, times(1)).getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
    }

    @Test
    @DisplayName("Should return null when the Authorization header does not start with 'Bearer '")
    void resolveTokenWhenAuthorizationHeaderDoesNotStartWithBearer() {
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Token abc123");

        var token = ReflectionTestUtils.invokeMethod(jwtFilter, "resolveToken", request);

        assertNull(token);
    }

    @Test
    @DisplayName("Should return the token when the Authorization header starts with 'Bearer ' and is not empty")
    void resolveTokenWhenAuthorizationHeaderStartsWithBearerAndIsNotEmpty() {
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer token");

        var token = (String) ReflectionTestUtils.invokeMethod(jwtFilter, "resolveToken", request);

        assertEquals("token", token);
    }

    @Test
    @DisplayName("Should track invalid signature and throw exception when the token has invalid signature")
    void validateTokenWhenTokenHasInvalidSignatureThenTrackAndThrowException() {
        var invalidToken = "invalidToken";

        when(jwtDecoder.decode(invalidToken)).thenThrow(new RuntimeException("Invalid signature"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(invalidToken));

        verify(securityMetersService, times(1)).trackTokenInvalidSignature();
    }

    @Test
    @DisplayName("Should track token malformed and throw exception when the token is unsecured/JWS/JWE")
    void validateTokenWhenTokenIsUnsecuredJwsJweThenTrackAndThrowException() {
        var token = "invalid_token";

        when(jwtDecoder.decode(token)).thenThrow(new RuntimeException("Invalid unsecured/JWS/JWE"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(token));
        verify(securityMetersService, times(1)).trackTokenMalformed();
    }

    @Test
    @DisplayName("Should track token expired and throw exception when the token is expired")
    void validateTokenWhenTokenIsExpiredThenTrackAndThrowException() {
        var expiredToken = "expired_token";

        when(jwtDecoder.decode(expiredToken)).thenThrow(new RuntimeException("Jwt expired at"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(expiredToken));

        verify(securityMetersService, times(1)).trackTokenExpired();
    }

    @Test
    @DisplayName("Should track token malformed and throw exception when the token is malformed")
    void validateTokenWhenTokenIsMalformedThenTrackAndThrowException() {
        var malformedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        when(jwtDecoder.decode(malformedToken)).thenThrow(new RuntimeException("Invalid JWT serialization"));

        assertThrows(RuntimeException.class, () -> jwtFilter.validateToken(malformedToken));
        verify(securityMetersService, times(1)).trackTokenMalformed();
    }
}
