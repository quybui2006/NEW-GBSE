package demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // ✅ Danh sách loại file ảnh được phép
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp"
    );

    // ✅ Danh sách đuôi file được phép (kiểm tra cả 2 lớp)
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    // ✅ Giới hạn kích thước tối đa: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String luuFile(MultipartFile file) throws IOException {
        // ========== BƯỚC 1: Kiểm tra file có rỗng không ==========
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        // ========== BƯỚC 2: Kiểm tra kích thước ==========
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                "File quá lớn. Kích thước tối đa là 5MB. File của bạn: " + 
                String.format("%.2f", file.getSize() / (1024.0 * 1024.0)) + "MB"
            );
        }

        // ========== BƯỚC 3: Kiểm tra content type ==========
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                "Loại file không được phép. Chỉ chấp nhận: JPG, PNG, GIF, WebP"
            );
        }

        // ========== BƯỚC 4: Kiểm tra đuôi file ==========
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                "Đuôi file không được phép. Chỉ chấp nhận: .jpg, .jpeg, .png, .gif, .webp"
            );
        }

        // ========== BƯỚC 5: Làm sạch tên file (chống path traversal) ==========
        // Chỉ giữ chữ cái, số, dấu chấm, dấu gạch ngang
        String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9.]", "_");
        // Không cho phép .. (path traversal)
        safeFilename = safeFilename.replace("..", "_");

        // ========== BƯỚC 6: Tạo tên file duy nhất ==========
        String tenFile = UUID.randomUUID() + "_" + safeFilename;

        // ========== BƯỚC 7: Tạo thư mục nếu chưa tồn tại ==========
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // ========== BƯỚC 8: Kiểm tra lần cuối đường dẫn an toàn ==========
        Path filePath = uploadPath.resolve(tenFile).normalize();
        // Đảm bảo filePath nằm trong uploadDir (chống path traversal)
        if (!filePath.startsWith(uploadPath.normalize())) {
            throw new SecurityException("Đường dẫn file không hợp lệ");
        }

        // ========== BƯỚC 9: Lưu file ==========
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return tenFile;
    }
}