package vn.fpt.assignment_datpd11.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fpt.assignment_datpd11.dto.request.LoginRequest;
import vn.fpt.assignment_datpd11.dto.request.RegisterRequest;
import vn.fpt.assignment_datpd11.dto.response.ApiResponse;
import vn.fpt.assignment_datpd11.dto.response.AuthResponse;
import vn.fpt.assignment_datpd11.service.AuthService;

/**
 * Controller xử lý các request liên quan đến xác thực người dùng
 * 
 * Endpoints:
 * - POST /api/v1/auth/register - Đăng ký tài khoản mới
 * - POST /api/v1/auth/login - Đăng nhập
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Đăng ký tài khoản người dùng mới
     * 
     * @param request Thông tin đăng ký (email, password, fullName)
     * @return ResponseEntity chứa JWT token và thông tin người dùng
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Đăng nhập người dùng
     * 
     * @param request Thông tin đăng nhập (email, password)
     * @return ResponseEntity chứa JWT token và thông tin người dùng
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid credentials"));
        }
    }
}

