package vn.fpt.assignment_datpd11.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO chứa thông tin yêu cầu đăng ký tài khoản mới
 * 
 * Được sử dụng trong endpoint POST /api/v1/auth/register
 * Tất cả các trường đều bắt buộc và được validate
 */
@Data
public class RegisterRequest {
    /**
     * Email của người dùng (bắt buộc, phải đúng định dạng email)
     * Email này sẽ được sử dụng làm tên đăng nhập
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Mật khẩu của người dùng (bắt buộc, tối thiểu 6 ký tự)
     * Mật khẩu sẽ được mã hóa bằng BCrypt trước khi lưu vào database
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /**
     * Họ và tên đầy đủ của người dùng (bắt buộc)
     */
    @NotBlank(message = "Full name is required")
    private String fullName;
}

