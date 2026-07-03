package com.devlinker.backend.global;

import com.devlinker.backend.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/api/health")
    public ApiResponse<String> health(){
        return ApiResponse.ok("헬스체크 성공", "DevLinker backend is running");
    }
}