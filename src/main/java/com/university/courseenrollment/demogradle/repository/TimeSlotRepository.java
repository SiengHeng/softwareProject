package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    Optional<TimeSlot> findByStartTimeAndEndTime(LocalTime startTime, LocalTime endTime);
    List<TimeSlot> findAllByOrderByStartTimeAsc();
}
