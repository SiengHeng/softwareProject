package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.enums.DayOfWeek;
import com.university.courseenrollment.demogradle.model.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.util.List;
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByCourseId(Long courseId);
    List<Schedule> findByClassroomId(Long classroomId);
    List<Schedule> findByDayOfWeek(DayOfWeek dayOfWeek);
    
    @Query("SELECT s FROM Schedule s WHERE s.classroom.id = :classroomId AND s.dayOfWeek = :dayOfWeek " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<Schedule> findConflictingSchedules(@Param("classroomId") Long classroomId, 
                                           @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                           @Param("startTime") LocalTime startTime, 
                                           @Param("endTime") LocalTime endTime);
}
