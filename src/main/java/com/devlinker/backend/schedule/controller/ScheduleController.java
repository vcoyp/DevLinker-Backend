package com.devlinker.backend.schedule.controller;

import com.devlinker.backend.global.response.ApiResponse;
import com.devlinker.backend.schedule.dto.ScheduleCreateRequest;
import com.devlinker.backend.schedule.dto.ScheduleResponse;
import com.devlinker.backend.schedule.service.ScheduleService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 일정 작성
    @PostMapping("/api/teams/{teamId}/schedules")
    public ApiResponse<ScheduleResponse> createSchedule(
            Authentication authentication,
            @PathVariable Long teamId,
            @RequestBody ScheduleCreateRequest request
    ) {
        ScheduleResponse response = scheduleService.createSchedule(
                authentication.getName(),
                teamId,
                request
        );

        return ApiResponse.ok("일정 작성 성공", response);
    }

    // 팀 일정 목록 조회
    @GetMapping("/api/teams/{teamId}/schedules")
    public ApiResponse<List<ScheduleResponse>> getSchedules(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        List<ScheduleResponse> response = scheduleService.getSchedules(
                authentication.getName(),
                teamId
        );

        return ApiResponse.ok("일정 목록 조회 성공", response);
    }

    // 일정 상세 조회
    @GetMapping("/api/schedules/{scheduleId}")
    public ApiResponse<ScheduleResponse> getSchedule(
            Authentication authentication,
            @PathVariable Long scheduleId
    ) {
        ScheduleResponse response = scheduleService.getSchedule(
                authentication.getName(),
                scheduleId
        );

        return ApiResponse.ok("일정 상세 조회 성공", response);
    }

    // 일정 삭제
    @DeleteMapping("/api/schedules/{scheduleId}")
    public ApiResponse<Void> deleteSchedule(
            Authentication authentication,
            @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(
                authentication.getName(),
                scheduleId
        );

        return ApiResponse.ok("일정 삭제 성공", null);
    }
}