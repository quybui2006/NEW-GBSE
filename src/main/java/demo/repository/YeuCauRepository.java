package demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import demo.model.YeuCau;

@Repository
public interface YeuCauRepository extends JpaRepository<YeuCau, Long> {

    // Closed date range: [from, to)
    @Query("SELECT y FROM YeuCau y WHERE y.thoiGian >= :from AND y.thoiGian < :to ORDER BY y.thoiGian DESC")
    List<YeuCau> findByDateRange(@Param("from") LocalDateTime from,
                                 @Param("to") LocalDateTime to);

    // Open-ended: >= from
    @Query("SELECT y FROM YeuCau y WHERE y.thoiGian >= :from ORDER BY y.thoiGian DESC")
    List<YeuCau> findByDateRangeFrom(@Param("from") LocalDateTime from);

    // Paginated closed range — used for admin request list
    @Query(value      = "SELECT y FROM YeuCau y WHERE y.thoiGian >= :from AND y.thoiGian < :to ORDER BY y.thoiGian DESC",
           countQuery = "SELECT COUNT(y) FROM YeuCau y WHERE y.thoiGian >= :from AND y.thoiGian < :to")
    Page<YeuCau> findByDateRangePaged(@Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to,
                                      Pageable pageable);

    // Dashboard aggregation via GROUP BY — avoids loading full rows into memory.
    // Returns Object[]{java.sql.Date day, Long count}.
    @Query(value = "SELECT DATE(thoi_gian) AS day, COUNT(*) AS cnt "
                 + "FROM yeu_cau "
                 + "WHERE thoi_gian >= :from AND thoi_gian < :to "
                 + "GROUP BY DATE(thoi_gian) "
                 + "ORDER BY day",
           nativeQuery = true)
    List<Object[]> countByDay(@Param("from") LocalDateTime from,
                               @Param("to") LocalDateTime to);

    Page<YeuCau> findAll(Pageable pageable);
}
