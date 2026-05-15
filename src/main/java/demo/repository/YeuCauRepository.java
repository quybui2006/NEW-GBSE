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

    // ✅ CÁCH 1: Tách thành 2 trường hợp riêng biệt
    @Query("SELECT y FROM YeuCau y WHERE y.thoiGian >= :from AND y.thoiGian < :to ORDER BY y.thoiGian DESC")
    List<YeuCau> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    
    @Query("SELECT y FROM YeuCau y WHERE y.thoiGian >= :from ORDER BY y.thoiGian DESC")
    List<YeuCau> findByDateRangeFrom(@Param("from") LocalDateTime from);

    @Query("SELECT y FROM YeuCau y WHERE y.thoiGian >= :from AND y.thoiGian < :to ORDER BY y.thoiGian DESC")
    List<YeuCau> findByDateRangeNative(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT y FROM YeuCau y WHERE y.thoiGian >= :from AND y.thoiGian < :to ORDER BY y.thoiGian DESC",
           countQuery = "SELECT COUNT(y) FROM YeuCau y WHERE y.thoiGian >= :from AND y.thoiGian < :to")
    Page<YeuCau> findByDateRangeCoPhanTrang(@Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to,
                                            Pageable pageable);

    // Phân trang tất cả
    Page<YeuCau> findAll(Pageable pageable);
}