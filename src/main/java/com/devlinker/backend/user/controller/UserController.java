package com.devlinker.backend.user.controller;

import com.devlinker.backend.auth.security.CustomUserDetails;
import com.devlinker.backend.global.response.ApiResponse;
import com.devlinker.backend.user.dto.UserMeResponse;
import com.devlinker.backend.user.entity.User;
import com.devlinker.backend.user.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return ApiResponse.ok("내 정보 조회 성공", UserMeResponse.from(user));
    }
}