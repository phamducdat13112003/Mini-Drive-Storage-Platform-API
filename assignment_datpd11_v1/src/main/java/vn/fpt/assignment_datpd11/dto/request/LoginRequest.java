package vn.fpt.assignment_datpd11.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO chứa thông tin yêu cầu đăng nhập
 * 
 * Được sử dụng trong endpoint POST /api/v1/auth/login
 * Tất cả các trường đều bắt buộc và được validate
 */
@Data
public class LoginRequest {
    /**
     * Email của người dùng (bắt buộc, phải đúng định dạng email)
     * Email này được sử dụng để tìm kiếm tài khoản trong database
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Mật khẩu của người dùng (bắt buộc)
     * Mật khẩu sẽ được so sánh với mật khẩu đã mã hóa trong database
     */
    @NotBlank(message = "Password is required")
    private String password;
}

