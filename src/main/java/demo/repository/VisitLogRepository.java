package demo.repository;

import demo.model.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {
    Optional<VisitLog> findByVisitDate(LocalDate visitDate);
    List<VisitLog> findByVisitDateBetween(LocalDate startDate, LocalDate endDate);
}