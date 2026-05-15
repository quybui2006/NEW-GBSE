package demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import demo.model.VisitLog;
import jakarta.persistence.LockModeType;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    Optional<VisitLog> findByVisitDate(LocalDate visitDate);

    // Acquires a row-level write lock — must be called inside a @Transactional method.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VisitLog v WHERE v.visitDate = :date")
    Optional<VisitLog> findByVisitDateForUpdate(@Param("date") LocalDate date);

    // Used by DashboardService to avoid a full-table findAll().
    List<VisitLog> findByVisitDateBetween(LocalDate from, LocalDate to);
}
