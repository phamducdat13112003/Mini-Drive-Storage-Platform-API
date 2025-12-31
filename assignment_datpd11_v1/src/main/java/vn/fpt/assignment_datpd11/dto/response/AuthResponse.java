package vn.fpt.assignment_datpd11.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa thông tin phản hồi sau khi đăng ký hoặc đăng nhập thành công
 * 
 * Được trả về từ các endpoint:
 * - POST /api/v1/auth/register
 * - POST /api/v1/auth/login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    /**
     * JWT token để xác thực các request tiếp theo
     * Token này cần được gửi kèm trong header Authorization: Bearer {token}
     */
    private String token;

    /**
     * Email của người dùng
     */
    private String email;

    /**
     * Họ và tên đầy đủ của người dùng
     */
    private String fullName;

    /**
     * ID của người dùng trong hệ thống
     */
    private Long userId;
}

