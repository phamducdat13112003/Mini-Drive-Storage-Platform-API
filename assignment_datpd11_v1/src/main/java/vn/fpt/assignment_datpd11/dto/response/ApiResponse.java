package vn.fpt.assignment_datpd11.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chuẩn cho tất cả các phản hồi API
 * 
 * Cung cấp cấu trúc thống nhất cho tất cả các endpoint:
 * - Thành công: success = true, có data và message (tùy chọn)
 * - Lỗi: success = false, có error message
 * 
 * @param <T> Kiểu dữ liệu của data trong phản hồi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    /**
     * Cờ đánh dấu yêu cầu thành công hay thất bại
     */
    private boolean success;

    /**
     * Thông báo mô tả kết quả (thường dùng cho trường hợp thành công)
     */
    private String message;

    /**
     * Dữ liệu trả về (có thể là bất kỳ kiểu nào)
     */
    private T data;

    /**
     * Thông báo lỗi (chỉ có khi success = false)
     */
    private String error;

    /**
     * Tạo phản hồi thành công với data
     * 
     * @param data dữ liệu trả về
     * @return ApiResponse với success = true và data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * Tạo phản hồi thành công với message và data
     * 
     * @param message thông báo mô tả
     * @param data dữ liệu trả về
     * @return ApiResponse với success = true, message và data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Tạo phản hồi lỗi với thông báo lỗi
     * 
     * @param error thông báo lỗi
     * @return ApiResponse với success = false và error message
     */
    public static <T> ApiResponse<T> error(String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .build();
    }
}

