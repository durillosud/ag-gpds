package br.com.gpds.security.jwt;

import br.com.gpds.management.SecurityMetersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 */
public class JWTFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String SESSION_KEY = "X-SessionKey";

    private final JwtDecoder jwtDecoder;
    private final SecurityMetersService metersService;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public JWTFilter(
        JwtDecoder jwtDecoder, SecurityMetersService metersService,
        JwtAuthenticationConverter jwtAuthenticationConverter
    ) {
        this.jwtDecoder = jwtDecoder;
        this.metersService = metersService;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        if (checkedHttpOptionsHeaderFromRequest(httpServletRequest)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            httpServletRequest
                .getRequestDispatcher(httpServletRequest.getServletPath())
                .forward(httpServletRequest, httpServletResponse);
        } else {
            var resolvedJwt = resolveToken(httpServletRequest);

            if (Objects.nonNull(resolvedJwt) && validateToken(resolvedJwt)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                var token = jwtAuthenticationConverter.convert(jwtDecoder.decode(resolvedJwt));
                token.setAuthenticated(true);

                securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                        token.getPrincipal(), token.getCredentials()
                    )
                );
                SecurityContextHolder.setContext(securityContext);
            }

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e) {
            if (e.getMessage().contains("Invalid signature")) {
                metersService.trackTokenInvalidSignature();
            } else if (e.getMessage().contains("Jwt expired at")) {
                metersService.trackTokenExpired();
            } else if (e.getMessage().contains("Invalid JWT serialization")) {
                metersService.trackTokenMalformed();
            } else if (e.getMessage().contains("Invalid unsecured/JWS/JWE")) {
                metersService.trackTokenMalformed();
            }
            throw e;
        }
    }

    /**
     * Case the gateway sends a custom header like {@link JWTFilter#SESSION_KEY}, it'll send an OPTIONS header first,
     * then here it is checked to pass forward a {@link HttpServletResponse#SC_OK} to response, and after that the
     * request would flow as expected
     *
     * @param httpServletRequest
     * @return boolean
     */
    boolean checkedHttpOptionsHeaderFromRequest(HttpServletRequest httpServletRequest) {
        return (
            HttpMethod.OPTIONS.matches(httpServletRequest.getMethod()) &&
                (
                    httpServletRequest
                        .getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS)
                        .toLowerCase()
                        .contains(SESSION_KEY.toLowerCase()) ||
                        httpServletRequest
                            .getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS)
                            .toLowerCase()
                            .contains(HttpHeaders.AUTHORIZATION.toLowerCase())
                )
        );
    }

    private String resolveToken(HttpServletRequest request) {
        var bearerToken = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER)).orElse("");
        if (!(bearerToken.isBlank() || bearerToken.isEmpty()) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
