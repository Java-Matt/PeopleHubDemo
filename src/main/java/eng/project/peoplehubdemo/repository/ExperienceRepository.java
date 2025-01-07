package eng.project.peoplehubdemo.repository;

import eng.project.peoplehubdemo.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ExperienceRepository extends JpaRepository<Experience, Integer> {
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM Experience e WHERE e.person.id = :personId AND " +
            "(:startDate BETWEEN e.startDate AND e.endDate OR " +
            ":endDate BETWEEN e.startDate AND e.endDate OR " +
            "e.startDate BETWEEN :startDate AND :endDate OR " +
            "e.endDate BETWEEN :startDate AND :endDate)")
    boolean existsOverlappingExperiences(@Param("personId") int personId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
