package vn.fpt.assignment_datpd11.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa thống kê sử dụng storage của người dùng
 * 
 * Được sử dụng trong endpoint GET /api/v1/analytics/usage
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageStatsResponse {
    /**
     * Tổng số file của người dùng
     */
    private Long totalFiles;

    /**
     * Tổng số thư mục của người dùng
     */
    private Long totalFolders;

    /**
     * Tổng dung lượng sử dụng tính bằng bytes
     */
    private Long totalSize;

    /**
     * Tổng dung lượng sử dụng đã được định dạng (ví dụ: "1.5 GB", "500 MB")
     */
    private String totalSizeFormatted;
}

