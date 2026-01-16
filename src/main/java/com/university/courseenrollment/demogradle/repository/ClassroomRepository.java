package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByRoomNumber(String roomNumber);
    List<Classroom> findByBuilding(String building);
    List<Classroom> findByIsAvailable(boolean isAvailable);
    boolean existsByRoomNumber(String roomNumber);
}
