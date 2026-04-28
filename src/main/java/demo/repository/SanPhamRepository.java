package demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import demo.model.SanPham;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    // Tìm kiếm (giữ nguyên)
    @Query("SELECT s FROM SanPham s WHERE LOWER(s.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.moTa) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.danhMuc) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SanPham> timKiem(@Param("keyword") String keyword);

    // ✅ THÊM: Phân trang tất cả sản phẩm
    Page<SanPham> findAll(Pageable pageable);

    // ✅ THÊM: Tìm kiếm + phân trang
    @Query("SELECT s FROM SanPham s WHERE LOWER(s.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.moTa) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.danhMuc) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SanPham> timKiemCoPhanTrang(@Param("keyword") String keyword, Pageable pageable);
}