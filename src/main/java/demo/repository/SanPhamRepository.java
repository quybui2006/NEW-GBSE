package demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import demo.model.SanPham;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

  @Query("SELECT s FROM SanPham s WHERE LOWER(s.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.moTa) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.danhMuc) LIKE LOWER(CONCAT('%', :keyword, '%'))")
List<SanPham> timKiem(String keyword);
}