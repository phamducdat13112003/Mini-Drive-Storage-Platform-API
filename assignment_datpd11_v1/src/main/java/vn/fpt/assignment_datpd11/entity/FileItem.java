package vn.fpt.assignment_datpd11.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity đại diện cho file hoặc thư mục trong hệ thống
 * 
 * Lưu trữ thông tin về:
 * - File: tên, đường dẫn, kích thước, loại MIME
 * - Thư mục: cấu trúc phân cấp (parent-child)
 * - Quyền sở hữu và chia sẻ
 * - Trạng thái xóa (soft delete)
 */
@Entity
@Table(name = "file_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileItem {
    /**
     * ID duy nhất của file/thư mục (tự động tăng)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tên của file hoặc thư mục (không được để trống)
     */
    @Column(nullable = false)
    private String name;

    /**
     * Loại: FILE hoặc FOLDER (không được để trống)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType type;

    /**
     * Đường dẫn vật lý đến file trên hệ thống
     * Chỉ có giá trị khi type = FILE
     */
    @Column(name = "file_path")
    private String filePath;

    /**
     * Kích thước file tính bằng bytes
     * Chỉ có giá trị khi type = FILE
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * Loại MIME của file (ví dụ: image/png, application/pdf)
     * Chỉ có giá trị khi type = FILE
     */
    @Column(name = "mime_type")
    private String mimeType;

    /**
     * Thư mục cha chứa file/thư mục này
     * null nếu là file/thư mục ở root
     * Quan hệ nhiều-một: nhiều file có thể thuộc một thư mục
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private FileItem parent;

    /**
     * Danh sách các file và thư mục con
     * Chỉ có giá trị khi type = FOLDER
     * Quan hệ một-nhiều: một thư mục có thể chứa nhiều file/thư mục
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileItem> children;

    /**
     * Người dùng sở hữu file/thư mục này (không được để trống)
     * Quan hệ nhiều-một: nhiều file có thể thuộc một người dùng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Danh sách các quyền truy cập được chia sẻ cho file/thư mục này
     * Quan hệ một-nhiều: một file có thể được chia sẻ cho nhiều người
     */
    @OneToMany(mappedBy = "fileItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FilePermission> permissions;

    /**
     * Cờ đánh dấu file/thư mục đã bị xóa (soft delete)
     * Mặc định là false
     */
    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    /**
     * Thời điểm file/thư mục bị xóa
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Thời điểm file/thư mục được tạo
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Thời điểm file/thư mục được cập nhật lần cuối
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum định nghĩa loại của item: FILE hoặc FOLDER
     */
    public enum FileType {
        /** File thông thường */
        FILE, 
        /** Thư mục */
        FOLDER
    }

    /**
     * Tự động thiết lập thời gian tạo, cập nhật và trạng thái xóa khi entity được lưu lần đầu
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    /**
     * Tự động cập nhật thời gian cập nhật mỗi khi entity được chỉnh sửa
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

