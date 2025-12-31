package vn.fpt.assignment_datpd11.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO chứa thông tin file hoặc thư mục để trả về cho client
 * 
 * Được sử dụng trong các endpoint:
 * - GET /api/v1/files (danh sách file)
 * - POST /api/v1/files (upload file hoặc tạo folder)
 * - Các endpoint khác liên quan đến file
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileItemResponse {
    /**
     * ID duy nhất của file/thư mục
     */
    private Long id;

    /**
     * Tên của file hoặc thư mục
     */
    private String name;

    /**
     * Loại: "FILE" hoặc "FOLDER"
     */
    private String type;

    /**
     * Kích thước file tính bằng bytes
     * null nếu là thư mục
     */
    private Long fileSize;

    /**
     * Loại MIME của file (ví dụ: image/png, application/pdf)
     * null nếu là thư mục
     */
    private String mimeType;

    /**
     * ID của thư mục cha
     * null nếu file/thư mục ở root
     */
    private Long parentId;

    /**
     * Email của người sở hữu file/thư mục
     */
    private String ownerEmail;

    /**
     * Họ và tên của người sở hữu file/thư mục
     */
    private String ownerName;

    /**
     * Thời điểm file/thư mục được tạo
     */
    private LocalDateTime createdAt;

    /**
     * Thời điểm file/thư mục được cập nhật lần cuối
     */
    private LocalDateTime updatedAt;
}

