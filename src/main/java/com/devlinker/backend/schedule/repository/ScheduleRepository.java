package com.devlinker.backend.schedule.repository;

import com.devlinker.backend.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByTeamIdOrderByStartAtAsc(Long teamId);
}