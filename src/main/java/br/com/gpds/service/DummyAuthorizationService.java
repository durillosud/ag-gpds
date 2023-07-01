package br.com.gpds.service;

import br.com.gpds.domain.response.JwtResponse;
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
    private final JHipsterProperties jHipsterProperties;
    private final long tokenValidityInMilliseconds;
    private final long tokenValidityInMillisecondsForRememberMe;

    public DummyAuthorizationService(JwtEncoder jwtEncoder, JHipsterProperties jHipsterProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jHipsterProperties = jHipsterProperties;

        this.tokenValidityInMilliseconds = 1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe =
            1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();
    }

    public JwtResponse getJwtForDummyUser(boolean rememberMe) {
        var now = Instant.now();

        var validity = Instant.now();
        if (rememberMe) {
            validity = validity.plusMillis(this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = validity.plusMillis(this.tokenValidityInMilliseconds);
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

    public long getTokenValidityInMilliseconds() {
        return tokenValidityInMilliseconds;
    }

    public long getTokenValidityInMillisecondsForRememberMe() {
        return tokenValidityInMillisecondsForRememberMe;
    }

    public JwtEncoder getJwtEncoder() {
        return jwtEncoder;
    }

    public JHipsterProperties getjHipsterProperties() {
        return jHipsterProperties;
    }
}
