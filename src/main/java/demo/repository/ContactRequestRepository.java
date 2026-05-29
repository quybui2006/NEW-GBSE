package demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import demo.model.ContactRequest;

@Repository
public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {

    // Closed date range: [from, to)
    @Query("SELECT r FROM ContactRequest r WHERE r.createdAt >= :from AND r.createdAt < :to ORDER BY r.createdAt DESC")
    List<ContactRequest> findByDateRange(@Param("from") LocalDateTime from,
                                         @Param("to") LocalDateTime to);

    // Open-ended: >= from
    @Query("SELECT r FROM ContactRequest r WHERE r.createdAt >= :from ORDER BY r.createdAt DESC")
    List<ContactRequest> findByDateRangeFrom(@Param("from") LocalDateTime from);

    // Paginated closed range — used for the admin request list
    @Query(value      = "SELECT r FROM ContactRequest r WHERE r.createdAt >= :from AND r.createdAt < :to ORDER BY r.createdAt DESC",
           countQuery = "SELECT COUNT(r) FROM ContactRequest r WHERE r.createdAt >= :from AND r.createdAt < :to")
    Page<ContactRequest> findByDateRangePaged(@Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to,
                                              Pageable pageable);

    // Dashboard aggregation via GROUP BY — avoids loading full rows into memory.
    // Returns Object[]{java.sql.Date day, Long count}. Physical column is thoi_gian.
    @Query(value = "SELECT DATE(thoi_gian) AS day, COUNT(*) AS cnt "
                 + "FROM yeu_cau "
                 + "WHERE thoi_gian >= :from AND thoi_gian < :to "
                 + "GROUP BY DATE(thoi_gian) "
                 + "ORDER BY day",
           nativeQuery = true)
    List<Object[]> countByDay(@Param("from") LocalDateTime from,
                              @Param("to") LocalDateTime to);

    Page<ContactRequest> findAll(Pageable pageable);
}
