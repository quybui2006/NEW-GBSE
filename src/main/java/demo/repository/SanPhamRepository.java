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

    // ✅ NATIVE QUERY - Hoạt động chắc chắn với PostgreSQL
    @Query(value = "SELECT * FROM yeu_cau WHERE thoi_gian >= :from AND (CAST(:to AS TIMESTAMP) IS NULL OR thoi_gian < :to) ORDER BY thoi_gian DESC", 
           nativeQuery = true)
    List<YeuCau> findByDateRangeNative(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    
    // ✅ Native query với phân trang
    @Query(value = "SELECT * FROM yeu_cau WHERE thoi_gian >= :from AND (CAST(:to AS TIMESTAMP) IS NULL OR thoi_gian < :to) ORDER BY thoi_gian DESC",
           countQuery = "SELECT COUNT(*) FROM yeu_cau WHERE thoi_gian >= :from AND (CAST(:to AS TIMESTAMP) IS NULL OR thoi_gian < :to)",
           nativeQuery = true)
    Page<YeuCau> findByDateRangeCoPhanTrang(@Param("from") LocalDateTime from, 
                                            @Param("to") LocalDateTime to, 
                                            Pageable pageable);
    
    // ✅ Các method phụ trợ (nếu dùng cách tách method)
    List<YeuCau> findByThoiGianBetween(LocalDateTime from, LocalDateTime to);
    
    List<YeuCau> findByThoiGianGreaterThanEqual(LocalDateTime from);
    
    // ✅ Phân trang tất cả
    Page<YeuCau> findAll(Pageable pageable);
}