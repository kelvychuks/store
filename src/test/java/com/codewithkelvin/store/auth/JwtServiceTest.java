package com.codewithkelvin.store.auth;

import com.codewithkelvin.store.users.Role;
import com.codewithkelvin.store.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private JwtConfig config;

    private static final User USER = User.builder()
            .id(42L)
            .name("Demo Shopper")
            .email("demo@store.dev")
            .password("hashed")
            .role(Role.ADMIN)
            .build();

    @BeforeEach
    void setUp() {
        config = new JwtConfig();
        config.setSecret("a-test-secret-long-enough-for-hmac-sha256-signing");
        config.setAccessTokenExpiration(900);
        config.setRefreshTokenExpiration(604800);
        jwtService = new JwtService(config);
    }

    @Test
    @DisplayName("a token survives a round trip with its subject and role intact")
    void roundTripsUserIdAndRole() {
        var token = jwtService.generateAccessToken(USER).toString();

        var parsed = jwtService.parseToken(token);

        assertThat(parsed).isNotNull();
        assertThat(parsed.getUserId()).isEqualTo(42L);
        assertThat(parsed.getRole()).isEqualTo(Role.ADMIN);
        assertThat(parsed.isExpired()).isFalse();
    }

    @Test
    @DisplayName("an expired token is rejected rather than parsed")
    void rejectsExpiredTokens() {
        config.setAccessTokenExpiration(-60);
        var expired = jwtService.generateAccessToken(USER).toString();

        assertThat(jwtService.parseToken(expired)).isNull();
    }

    @Test
    @DisplayName("a token signed with a different secret is rejected")
    void rejectsTokensSignedWithAnotherSecret() {
        var foreignConfig = new JwtConfig();
        foreignConfig.setSecret("a-completely-different-secret-of-sufficient-length");
        foreignConfig.setAccessTokenExpiration(900);
        var foreignToken = new JwtService(foreignConfig).generateAccessToken(USER).toString();

        assertThat(jwtService.parseToken(foreignToken)).isNull();
    }

    @Test
    void rejectsGarbage() {
        assertThat(jwtService.parseToken("not-a-jwt")).isNull();
    }

    @Test
    void refreshTokensOutliveAccessTokens() {
        var access = jwtService.parseToken(jwtService.generateAccessToken(USER).toString());
        var refresh = jwtService.parseToken(jwtService.generateRefreshToken(USER).toString());

        assertThat(access).isNotNull();
        assertThat(refresh).isNotNull();
        assertThat(access.isExpired()).isFalse();
        assertThat(refresh.isExpired()).isFalse();
    }
}
