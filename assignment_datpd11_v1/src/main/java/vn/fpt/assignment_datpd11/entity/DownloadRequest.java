package vn.fpt.assignment_datpd11.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho yêu cầu tải xuống thư mục
 * 
 * Được sử dụng để quản lý quá trình tải xuống thư mục (zip file) bất đồng bộ:
 * - Theo dõi trạng thái xử lý (PENDING, PROCESSING, READY, FAILED)
 * - Lưu đường dẫn đến file zip đã tạo
 * - Lưu thông tin lỗi nếu quá trình xử lý thất bại
 */
@Entity
@Table(name = "download_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DownloadRequest {
    /**
     * ID duy nhất của yêu cầu tải xuống (UUID)
     * Được sử dụng để truy vấn trạng thái và tải file
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String requestId;

    /**
     * File hoặc thư mục cần tải xuống (không được để trống)
     * Quan hệ nhiều-một: nhiều yêu cầu có thể thuộc một file
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_item_id", nullable = false)
    private FileItem fileItem;

    /**
     * Người dùng yêu cầu tải xuống (không được để trống)
     * Quan hệ nhiều-một: một người dùng có thể có nhiều yêu cầu
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Trạng thái của yêu cầu tải xuống (không được để trống)
     * Mặc định là PENDING khi tạo mới
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DownloadStatus status;

    /**
     * Đường dẫn đến file zip đã được tạo
     * Chỉ có giá trị khi status = READY
     */
    @Column(name = "zip_file_path")
    private String zipFilePath;

    /**
     * Thông báo lỗi nếu quá trình xử lý thất bại
     * Chỉ có giá trị khi status = FAILED
     */
    @Column(name = "error_message")
    private String errorMessage;

    /**
     * Thời điểm yêu cầu được tạo
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Thời điểm yêu cầu được cập nhật lần cuối
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum định nghĩa trạng thái của yêu cầu tải xuống
     */
    public enum DownloadStatus {
        /** Đang chờ xử lý */
        PENDING, 
        /** Đang xử lý (đang tạo file zip) */
        PROCESSING, 
        /** Đã sẵn sàng (file zip đã được tạo) */
        READY, 
        /** Xử lý thất bại */
        FAILED
    }

    /**
     * Tự động thiết lập thời gian tạo, cập nhật và trạng thái khi entity được lưu lần đầu
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = DownloadStatus.PENDING;
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

