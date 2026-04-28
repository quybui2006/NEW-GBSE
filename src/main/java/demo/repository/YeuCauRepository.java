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

    // ✅ THÊM: Lọc theo ngày TRONG DATABASE (không lọc trong RAM nữa)
    @Query("SELECT y FROM YeuCau y WHERE y.thoiGian >= :from AND (:to IS NULL OR y.thoiGian < :to) ORDER BY y.thoiGian DESC")
    List<YeuCau> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // ✅ THÊM: Phân trang tất cả
    Page<YeuCau> findAll(Pageable pageable);

    // ✅ THÊM: Lọc + phân trang
    @Query("SELECT y FROM YeuCau y WHERE y.thoiGian >= :from AND (:to IS NULL OR y.thoiGian < :to) ORDER BY y.thoiGian DESC")
    Page<YeuCau> findByDateRangeCoPhanTrang(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);
}