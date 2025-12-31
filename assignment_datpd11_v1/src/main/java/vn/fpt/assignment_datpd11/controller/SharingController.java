package vn.fpt.assignment_datpd11.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.fpt.assignment_datpd11.dto.request.ShareRequest;
import vn.fpt.assignment_datpd11.dto.response.ApiResponse;
import vn.fpt.assignment_datpd11.dto.response.FileItemResponse;
import vn.fpt.assignment_datpd11.repository.UserRepository;
import vn.fpt.assignment_datpd11.service.SharingService;

import java.util.List;

/**
 * Controller xử lý các request liên quan đến chia sẻ file
 * 
 * Endpoints:
 * - POST /api/v1/files/{id}/share - Chia sẻ file/thư mục cho người dùng khác
 * - GET /api/v1/files/shared-with-me - Lấy danh sách file được chia sẻ cho người dùng
 */
@RestController
@RequestMapping("/api/v1/files")
public class SharingController {

    @Autowired
    private SharingService sharingService;

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
     * Chia sẻ file/thư mục cho một người dùng qua email
     * 
     * @param id ID của file/thư mục cần chia sẻ
     * @param request Thông tin chia sẻ (email, permission)
     * @param authentication Authentication object từ Spring Security
     * @return ResponseEntity chứa thông báo thành công
     */
    @PostMapping("/{id}/share")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> shareFile(
            @PathVariable Long id,
            @Valid @RequestBody ShareRequest request,
            Authentication authentication) {
        try {
            Long userId = getUserId(authentication);
            sharingService.shareFile(id, request.getEmail(), request.getPermission(), userId);
            return ResponseEntity.ok(ApiResponse.success("File shared successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Lấy danh sách file/thư mục được chia sẻ cho người dùng hiện tại
     * 
     * @param authentication Authentication object từ Spring Security
     * @return ResponseEntity chứa danh sách file/thư mục được chia sẻ
     */
    @GetMapping("/shared-with-me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<FileItemResponse>>> getSharedWithMe(Authentication authentication) {
        try {
            Long userId = getUserId(authentication);
            List<FileItemResponse> files = sharingService.getSharedWithMe(userId);
            return ResponseEntity.ok(ApiResponse.success(files));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

