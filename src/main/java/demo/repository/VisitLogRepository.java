package demo.repository;

import demo.model.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {
    Optional<VisitLog> findByVisitDate(LocalDate visitDate);
}