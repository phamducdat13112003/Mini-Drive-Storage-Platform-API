package vn.fpt.assignment_datpd11.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.fpt.assignment_datpd11.dto.response.UsageStatsResponse;
import vn.fpt.assignment_datpd11.entity.FileItem;
import vn.fpt.assignment_datpd11.repository.FileItemRepository;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Service xử lý các nghiệp vụ liên quan đến thống kê và phân tích
 * 
 * Cung cấp các chức năng:
 * - Tính toán thống kê sử dụng storage của người dùng
 * - Định dạng kích thước file (bytes sang KB, MB, GB, ...)
 */
@Service
public class AnalyticsService {

    @Autowired
    private FileItemRepository fileItemRepository;

    /**
     * Lấy thống kê sử dụng storage của người dùng
     * 
     * @param userId ID của người dùng
     * @return UsageStatsResponse chứa tổng số file, thư mục và dung lượng sử dụng
     */
    public UsageStatsResponse getUsageStats(Long userId) {
        List<FileItem> userFiles = fileItemRepository.findByOwnerIdAndIsDeletedFalse(userId);
        
        long totalFiles = userFiles.stream()
                .filter(f -> f.getType() == FileItem.FileType.FILE)
                .count();
        
        long totalFolders = userFiles.stream()
                .filter(f -> f.getType() == FileItem.FileType.FOLDER)
                .count();
        
        Long totalSize = fileItemRepository.getTotalSizeByOwner(userId);
        if (totalSize == null) {
            totalSize = 0L;
        }

        return UsageStatsResponse.builder()
                .totalFiles(totalFiles)
                .totalFolders(totalFolders)
                .totalSize(totalSize)
                .totalSizeFormatted(formatFileSize(totalSize))
                .build();
    }

    /**
     * Định dạng kích thước file từ bytes sang đơn vị lớn hơn (KB, MB, GB, ...)
     * 
     * @param bytes Kích thước tính bằng bytes
     * @return Chuỗi đã được định dạng (ví dụ: "1.5 MB", "500 KB")
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(bytes / Math.pow(1024, exp)) + " " + pre + "B";
    }
}

