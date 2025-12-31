package vn.fpt.assignment_datpd11.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO chứa thông tin yêu cầu chia sẻ file/thư mục
 * 
 * Được sử dụng trong endpoint POST /api/v1/sharing/{fileId}/share
 * Tất cả các trường đều bắt buộc và được validate
 */
@Data
public class ShareRequest {
    /**
     * Email của người dùng được chia sẻ (bắt buộc, phải đúng định dạng email)
     * Email này phải tồn tại trong hệ thống
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Mức độ quyền truy cập: "VIEW" hoặc "EDIT" (bắt buộc)
     * - VIEW: chỉ xem và tải xuống
     * - EDIT: xem, tải xuống, chỉnh sửa và xóa
     */
    @NotBlank(message = "Permission is required")
    private String permission; // VIEW or EDIT
}

