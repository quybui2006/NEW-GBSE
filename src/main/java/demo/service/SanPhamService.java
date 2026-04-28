package demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import demo.model.SanPham;
import demo.repository.SanPhamRepository;

@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private FileUploadService fileUploadService;

    // Tìm kiếm sản phẩm
    public List<SanPham> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return sanPhamRepository.findAll();
        }
        return sanPhamRepository.timKiem(keyword.trim());
    }

    // Lấy tất cả sản phẩm
    public List<SanPham> layTatCa() {
        return sanPhamRepository.findAll();
    }

    // Tìm sản phẩm theo ID
    public Optional<SanPham> timTheoId(Long id) {
        return sanPhamRepository.findById(id);
    }

    // Lưu sản phẩm (thêm mới hoặc cập nhật)
    public SanPham luu(SanPham sanPham, MultipartFile hinhAnhFile) {
        if (hinhAnhFile != null && !hinhAnhFile.isEmpty()) {
            try {
                String tenFile = fileUploadService.luuFile(hinhAnhFile);
                sanPham.setHinhAnh(tenFile);
            } catch (Exception e) {
                throw new RuntimeException("Không thể lưu ảnh: " + e.getMessage(), e);
            }
        }
        return sanPhamRepository.save(sanPham);
    }

    // Xóa sản phẩm
    public void xoa(Long id) {
        sanPhamRepository.deleteById(id);
    }

    // Đếm tổng sản phẩm
    public long demTong() {
        return sanPhamRepository.count();
    }
}