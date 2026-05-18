package demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;

    // Tika-detected MIME types that are actually safe image formats.
    // Content-Type from the browser is client-controlled and can be spoofed.
    // Tika reads the file's magic bytes — it cannot be faked.
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    private static final Tika tika = new Tika();

    public String luuFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(String.format(
                "File too large (%.1f MB). Maximum is 5 MB.",
                file.getSize() / (1024.0 * 1024.0)
            ));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Invalid filename");
        }

        String extension = "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot > 0) {
            extension = originalFilename.substring(dot).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                "Extension not allowed. Accepted: .jpg, .jpeg, .png, .gif, .webp");
        }

        // Read magic bytes via Tika — this is the only reliable check.
        // The browser Content-Type header is ignored intentionally.
        String detectedType;
        try (InputStream is = file.getInputStream()) {
            detectedType = tika.detect(is);
        }
        if (!ALLOWED_MIME_TYPES.contains(detectedType)) {
            throw new IllegalArgumentException(
                "File content does not match an allowed image type. Detected: " + detectedType);
        }

        // Sanitize filename and prevent path traversal
        String safeName = originalFilename.replaceAll("[^a-zA-Z0-9.]", "_").replace("..", "_");
        String uniqueName = UUID.randomUUID() + "_" + safeName;

        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        Path dest = uploadPath.resolve(uniqueName).normalize();
        if (!dest.startsWith(uploadPath.normalize())) {
            throw new SecurityException("Path traversal attempt detected");
        }

        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
        return uniqueName;
    }
}
