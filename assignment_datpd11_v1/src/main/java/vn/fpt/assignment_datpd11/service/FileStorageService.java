package vn.fpt.assignment_datpd11.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service xử lý lưu trữ file trên hệ thống
 * 
 * Cung cấp các chức năng:
 * - Lưu file upload vào thư mục của người dùng
 * - Lấy file từ đường dẫn
 * - Xóa file khỏi hệ thống
 * - Đảm bảo thư mục lưu trữ tồn tại
 */
@Service
public class FileStorageService {

    /**
     * Đường dẫn thư mục lưu trữ file (đọc từ application.properties)
     */
    @Value("${file.storage.path}")
    private String storagePath;

    /**
     * Lưu file upload vào hệ thống
     * 
     * File được lưu với tên duy nhất (UUID + tên gốc) trong thư mục của người dùng
     * Cấu trúc: {storagePath}/{userId}/{UUID}_{originalFilename}
     * 
     * @param file File cần lưu
     * @param userId ID của người dùng upload
     * @return Đường dẫn đầy đủ đến file đã lưu
     * @throws IOException nếu có lỗi khi tạo thư mục hoặc lưu file
     */
    public String saveFile(MultipartFile file, Long userId) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path userDir = Paths.get(storagePath, userId.toString());
        
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
        }
        
        Path filePath = userDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filePath.toString();
    }

    /**
     * Lấy file từ đường dẫn
     * 
     * @param filePath Đường dẫn đến file
     * @return File object
     */
    public File getFile(String filePath) {
        return new File(filePath);
    }

    /**
     * Xóa file khỏi hệ thống
     * 
     * @param filePath Đường dẫn đến file cần xóa
     * @throws IOException nếu có lỗi khi xóa file
     */
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    /**
     * Đảm bảo thư mục lưu trữ tồn tại
     * Tạo thư mục nếu chưa tồn tại
     * 
     * @throws IOException nếu có lỗi khi tạo thư mục
     */
    public void ensureDirectoriesExist() throws IOException {
        Path storageDir = Paths.get(storagePath);
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
    }
}

