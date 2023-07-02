package br.com.gpds.service;

import br.com.gpds.domain.response.JwtResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import tech.jhipster.config.JHipsterProperties;

import java.time.Instant;
import java.util.Collections;

import static br.com.gpds.security.AuthoritiesConstants.ANONYMOUS;
import static br.com.gpds.security.SecurityUtils.AUTHORITIES_KEY;
import static br.com.gpds.security.SecurityUtils.JWT_ALGORITHM;

@Service
public class DummyAuthorizationService {

    private final JwtEncoder jwtEncoder;
    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds}")
    private long tokenValidityInMilliseconds;
    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me}")
    private long tokenValidityInMillisecondsForRememberMe;

    public DummyAuthorizationService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public JwtResponse getJwtForDummyUser(boolean rememberMe) {
        var now = Instant.now();

        var validity = Instant.now();
        if (rememberMe) {
            validity = validity.plusMillis(1000 * this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = validity.plusMillis(1000 * this.tokenValidityInMilliseconds);
        }

        var claims = JwtClaimsSet
            .builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject("dummy")
            .claims(customClain -> customClain.put(AUTHORITIES_KEY, Collections.singletonList(ANONYMOUS)))
            .build();

        var jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        return new JwtResponse(
            this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue()
        );
    }

    public void setTokenValidityInMilliseconds(long tokenValidityInMilliseconds) {
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    public long getTokenValidityInMilliseconds() {
        return tokenValidityInMilliseconds;
    }

    public long getTokenValidityInMillisecondsForRememberMe() {
        return tokenValidityInMillisecondsForRememberMe;
    }

    public void setTokenValidityInMillisecondsForRememberMe(long tokenValidityInMillisecondsForRememberMe) {
        this.tokenValidityInMillisecondsForRememberMe = tokenValidityInMillisecondsForRememberMe;
    }
}
