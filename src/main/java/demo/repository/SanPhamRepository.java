package demo.repository;

import demo.model.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    @Query("SELECT s FROM SanPham s WHERE s.ten LIKE %:keyword% OR s.moTa LIKE %:keyword% OR s.danhMuc LIKE %:keyword%")
    List<SanPham> timKiem(String keyword);
}