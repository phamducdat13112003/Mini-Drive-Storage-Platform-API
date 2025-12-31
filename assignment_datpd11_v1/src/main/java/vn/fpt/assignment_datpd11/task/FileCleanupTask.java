package vn.fpt.assignment_datpd11.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.fpt.assignment_datpd11.entity.FileItem;
import vn.fpt.assignment_datpd11.repository.FileItemRepository;
import vn.fpt.assignment_datpd11.service.FileStorageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scheduled task để dọn dẹp các file đã bị xóa
 * 
 * Chạy định kỳ để:
 * - Tìm các file đã bị xóa (soft delete) quá lâu
 * - Xóa file vật lý khỏi hệ thống
 * - Xóa record khỏi database
 * 
 * Mặc định chạy mỗi ngày lúc 2:00 AM
 */
@Component
public class FileCleanupTask {

    @Autowired
    private FileItemRepository fileItemRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Số ngày giữ lại file đã xóa trước khi xóa vĩnh viễn (mặc định: 30 ngày)
     */
    @Value("${file.cleanup.retention-days:30}")
    private int retentionDays;

    /**
     * Thread pool để xử lý xóa file song song
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    /**
     * Dọn dẹp các file đã bị xóa quá lâu
     * 
     * Chạy theo lịch định kỳ (mặc định: mỗi ngày lúc 2:00 AM)
     * Cron expression: "0 0 2 * * ?" (có thể cấu hình qua file.cleanup.cron)
     */
    @Scheduled(cron = "${file.cleanup.cron:0 0 2 * * ?}")
    @Transactional
    public void cleanupDeletedFiles() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
        List<FileItem> deletedFiles = fileItemRepository.findDeletedBefore(cutoffDate);

        for (FileItem file : deletedFiles) {
            executorService.submit(() -> {
                try {
                    // Delete physical file
                    if (file.getFilePath() != null) {
                        fileStorageService.deleteFile(file.getFilePath());
                    }
                    // Delete from database
                    fileItemRepository.delete(file);
                } catch (IOException e) {
                    System.err.println("Error deleting file " + file.getId() + ": " + e.getMessage());
                }
            });
        }
    }
}

