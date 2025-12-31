package vn.fpt.assignment_datpd11.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpt.assignment_datpd11.dto.request.LoginRequest;
import vn.fpt.assignment_datpd11.dto.request.RegisterRequest;
import vn.fpt.assignment_datpd11.dto.response.AuthResponse;
import vn.fpt.assignment_datpd11.entity.User;
import vn.fpt.assignment_datpd11.repository.UserRepository;
import vn.fpt.assignment_datpd11.security.CustomUserDetailsService;
import vn.fpt.assignment_datpd11.security.JwtTokenProvider;

/**
 * Service xử lý các nghiệp vụ liên quan đến xác thực người dùng
 * 
 * Cung cấp các chức năng:
 * - Đăng ký tài khoản mới
 * - Đăng nhập và tạo JWT token
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Đăng ký tài khoản người dùng mới
     * 
     * Quy trình:
     * 1. Kiểm tra email đã tồn tại chưa
     * 2. Mã hóa mật khẩu bằng BCrypt
     * 3. Lưu người dùng vào database
     * 4. Tạo JWT token và trả về
     * 
     * @param request Thông tin đăng ký (email, password, fullName)
     * @return AuthResponse chứa JWT token và thông tin người dùng
     * @throws RuntimeException nếu email đã tồn tại
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();

        user = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtTokenProvider.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userId(user.getId())
                .build();
    }

    /**
     * Đăng nhập người dùng
     * 
     * Quy trình:
     * 1. Xác thực email và mật khẩu bằng AuthenticationManager
     * 2. Tạo JWT token
     * 3. Trả về token và thông tin người dùng
     * 
     * @param request Thông tin đăng nhập (email, password)
     * @return AuthResponse chứa JWT token và thông tin người dùng
     * @throws RuntimeException nếu thông tin đăng nhập không đúng
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtTokenProvider.generateToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userId(user.getId())
                .build();
    }
}

