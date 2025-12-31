package vn.fpt.assignment_datpd11.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa thông tin về yêu cầu tải xuống thư mục
 * 
 * Được sử dụng trong các endpoint:
 * - POST /api/v1/files/{id}/download (khởi tạo tải xuống)
 * - GET /api/v1/files/downloads/{requestId} (kiểm tra trạng thái)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownloadResponse {
    /**
     * ID duy nhất của yêu cầu tải xuống (UUID)
     * Được sử dụng để truy vấn trạng thái và tải file
     */
    private String requestId;

    /**
     * Trạng thái của yêu cầu: PENDING, PROCESSING, READY, FAILED
     */
    private String status;

    /**
     * URL để tải xuống file zip (chỉ có khi status = READY)
     * Format: /api/v1/files/downloads/{requestId}/file
     */
    private String downloadUrl;

    /**
     * Thông báo lỗi (chỉ có khi status = FAILED)
     */
    private String errorMessage;
}

