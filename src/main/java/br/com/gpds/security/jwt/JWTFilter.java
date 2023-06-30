package br.com.gpds.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.web.filter.*;

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

    private final TokenProvider tokenProvider;
    private static volatile String jwt;

    public JWTFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        if (checkedHttpOptionsHeaderFromRequest(httpServletRequest)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            httpServletRequest.getRequestDispatcher(httpServletRequest.getServletPath()).forward(httpServletRequest, httpServletResponse);
        } else {
            var resolvedJwt = resolveToken(httpServletRequest);

            if (Objects.nonNull(resolvedJwt) && this.tokenProvider.validateToken(resolvedJwt)) {
                Authentication authentication = this.tokenProvider.getAuthentication(resolvedJwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(servletRequest, servletResponse);
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
    private boolean checkedHttpOptionsHeaderFromRequest(HttpServletRequest httpServletRequest) {
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
