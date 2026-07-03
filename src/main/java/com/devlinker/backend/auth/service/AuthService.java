package com.devlinker.backend.auth.service;

import com.devlinker.backend.auth.dto.AuthTokenResponse;
import com.devlinker.backend.auth.dto.LoginRequest;
import com.devlinker.backend.auth.dto.ReissueRequest;
import com.devlinker.backend.auth.dto.SignUpRequest;
import com.devlinker.backend.auth.entity.RefreshToken;
import com.devlinker.backend.auth.jwt.JwtTokenProvider;
import com.devlinker.backend.auth.repository.RefreshTokenRepository;
import com.devlinker.backend.user.entity.User;
import com.devlinker.backend.user.entity.UserRole;
import com.devlinker.backend.user.entity.UserStatus;
import com.devlinker.backend.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                UserRole.USER,
                UserStatus.ACTIVE
        );

        userRepository.save(user);
    }

    @Transactional
    public AuthTokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getEmail());

        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        savedRefreshToken -> savedRefreshToken.updateToken(
                                refreshTokenValue,
                                getRefreshTokenExpiryAt()
                        ),
                        () -> refreshTokenRepository.save(
                                new RefreshToken(
                                        user,
                                        refreshTokenValue,
                                        getRefreshTokenExpiryAt()
                                )
                        )
                );

        return new AuthTokenResponse(accessToken, refreshTokenValue);
    }

    @Transactional
    public AuthTokenResponse reissue(ReissueRequest request) {
        String refreshTokenValue = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtTokenProvider.getEmail(refreshTokenValue);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        RefreshToken savedRefreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰이 없습니다."));

        if (!savedRefreshToken.getRefreshToken().equals(refreshTokenValue)) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        savedRefreshToken.updateToken(
                newRefreshToken,
                getRefreshTokenExpiryAt()
        );

        return new AuthTokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        refreshTokenRepository.deleteByUser(user);
    }

    private LocalDateTime getRefreshTokenExpiryAt() {
        return LocalDateTime.now()
                .plusNanos(jwtTokenProvider.getRefreshTokenExpiration() * 1_000_000);
    }
}