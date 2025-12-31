package vn.fpt.assignment_datpd11.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho quyền truy cập được chia sẻ
 * 
 * Lưu trữ thông tin về quyền truy cập của người dùng đối với file/thư mục:
 * - File/thư mục được chia sẻ
 * - Người dùng được chia sẻ
 * - Mức độ quyền truy cập (VIEW hoặc EDIT)
 * 
 * Ràng buộc duy nhất: một người dùng chỉ có một quyền đối với một file
 */
@Entity
@Table(name = "file_permissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"file_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilePermission {
    /**
     * ID duy nhất của quyền truy cập (tự động tăng)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * File hoặc thư mục được chia sẻ (không được để trống)
     * Quan hệ nhiều-một: nhiều quyền có thể thuộc một file
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private FileItem fileItem;

    /**
     * Người dùng được chia sẻ quyền truy cập (không được để trống)
     * Quan hệ nhiều-một: một người dùng có thể có nhiều quyền
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Mức độ quyền truy cập: VIEW (chỉ xem) hoặc EDIT (chỉnh sửa)
     * Không được để trống
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_level", nullable = false)
    private PermissionLevel permissionLevel;

    /**
     * Thời điểm quyền truy cập được tạo
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Thời điểm quyền truy cập được cập nhật lần cuối
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum định nghĩa mức độ quyền truy cập
     */
    public enum PermissionLevel {
        /** Quyền chỉ xem: có thể xem và tải xuống file */
        VIEW, 
        /** Quyền chỉnh sửa: có thể xem, tải xuống, chỉnh sửa và xóa file */
        EDIT
    }

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

