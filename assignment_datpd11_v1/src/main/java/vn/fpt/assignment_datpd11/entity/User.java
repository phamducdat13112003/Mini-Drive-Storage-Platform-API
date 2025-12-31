package vn.fpt.assignment_datpd11.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity đại diện cho người dùng trong hệ thống
 * 
 * Lưu trữ thông tin tài khoản người dùng bao gồm:
 * - Thông tin đăng nhập (email, password)
 * - Thông tin cá nhân (fullName)
 * - Quan hệ với các file và quyền truy cập
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    /**
     * ID duy nhất của người dùng (tự động tăng)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email của người dùng (duy nhất, không được để trống)
     * Được sử dụng làm tên đăng nhập
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Mật khẩu đã được mã hóa (không được để trống)
     * Mật khẩu được mã hóa bằng BCrypt trước khi lưu vào database
     */
    @Column(nullable = false)
    private String password;

    /**
     * Họ và tên đầy đủ của người dùng (không được để trống)
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * Thời điểm tài khoản được tạo
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Thời điểm tài khoản được cập nhật lần cuối
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Danh sách các file và thư mục mà người dùng sở hữu
     * Quan hệ một-nhiều: một người dùng có thể sở hữu nhiều file
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileItem> ownedFiles;

    /**
     * Danh sách các quyền truy cập mà người dùng được chia sẻ
     * Quan hệ một-nhiều: một người dùng có thể có nhiều quyền truy cập
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FilePermission> permissions;

    /**
     * Tự động thiết lập thời gian tạo và cập nhật khi entity được lưu lần đầu
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Tự động cập nhật thời gian cập nhật mỗi khi entity được chỉnh sửa
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

