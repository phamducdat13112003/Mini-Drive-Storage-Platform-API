package vn.fpt.assignment_datpd11.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.fpt.assignment_datpd11.dto.response.ApiResponse;
import vn.fpt.assignment_datpd11.dto.response.UsageStatsResponse;
import vn.fpt.assignment_datpd11.repository.UserRepository;
import vn.fpt.assignment_datpd11.service.AnalyticsService;

/**
 * Controller xử lý các request liên quan đến thống kê và phân tích
 * 
 * Endpoints:
 * - GET /api/v1/analytics/usage - Lấy thống kê sử dụng storage của người dùng
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy ID của người dùng từ Authentication object
     * 
     * @param authentication Authentication object từ Spring Security
     * @return ID của người dùng
     */
    private Long getUserId(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    /**
     * Lấy thống kê sử dụng storage của người dùng
     * 
     * Bao gồm:
     * - Tổng số file
     * - Tổng số thư mục
     * - Tổng dung lượng sử dụng (bytes và định dạng)
     * 
     * @param authentication Authentication object từ Spring Security
     * @return ResponseEntity chứa thống kê sử dụng
     */
    @GetMapping("/usage")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UsageStatsResponse>> getUsageStats(Authentication authentication) {
        try {
            Long userId = getUserId(authentication);
            UsageStatsResponse stats = analyticsService.getUsageStats(userId);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

