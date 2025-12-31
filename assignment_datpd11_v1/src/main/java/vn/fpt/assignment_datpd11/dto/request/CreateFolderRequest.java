package vn.fpt.assignment_datpd11.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO chứa thông tin yêu cầu tạo thư mục mới
 * 
 * Được sử dụng trong endpoint POST /api/v1/files (với Content-Type: application/json)
 */
@Data
public class CreateFolderRequest {
    /**
     * Tên của thư mục cần tạo (bắt buộc)
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * ID của thư mục cha (tùy chọn)
     * Nếu null hoặc rỗng, thư mục sẽ được tạo ở root
     */
    private String parentId;

    /**
     * Loại item (mặc định là "FOLDER")
     * Trường này thường không cần thiết vì endpoint này chỉ dùng để tạo thư mục
     */
    private String type = "FOLDER";
}

