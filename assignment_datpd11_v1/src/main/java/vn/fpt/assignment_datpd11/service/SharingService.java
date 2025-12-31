package vn.fpt.assignment_datpd11.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpt.assignment_datpd11.dto.response.FileItemResponse;
import vn.fpt.assignment_datpd11.entity.FileItem;
import vn.fpt.assignment_datpd11.entity.FilePermission;
import vn.fpt.assignment_datpd11.entity.User;
import vn.fpt.assignment_datpd11.repository.FileItemRepository;
import vn.fpt.assignment_datpd11.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý các nghiệp vụ liên quan đến chia sẻ file
 * 
 * Cung cấp các chức năng:
 * - Chia sẻ file/thư mục cho người dùng khác
 * - Gửi email thông báo khi chia sẻ
 * - Lấy danh sách file được chia sẻ cho người dùng
 */
@Service
public class SharingService {

    @Autowired
    private FileItemRepository fileItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileService fileService;

    /**
     * Chia sẻ file/thư mục cho một người dùng qua email
     * 
     * Quy trình:
     * 1. Kiểm tra người chia sẻ có quyền EDIT
     * 2. Tìm người dùng đích qua email
     * 3. Tạo/cập nhật quyền truy cập
     * 4. Gửi email thông báo
     * 
     * @param fileId ID của file/thư mục cần chia sẻ
     * @param targetEmail Email của người dùng được chia sẻ
     * @param permission Mức độ quyền: "VIEW" hoặc "EDIT"
     * @param sharerId ID của người dùng thực hiện chia sẻ
     * @throws RuntimeException nếu file không tồn tại, người dùng không tồn tại hoặc không có quyền chia sẻ
     */
    @Transactional
    public void shareFile(Long fileId, String targetEmail, String permission, Long sharerId) {
        FileItem fileItem = fileItemRepository.findByIdAndIsDeletedFalse(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!permissionService.hasAccess(sharerId, fileId, true)) {
            throw new RuntimeException("No permission to share this file");
        }

        User targetUser = userRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        User sharer = userRepository.findById(sharerId)
                .orElseThrow(() -> new RuntimeException("Sharer not found"));

        FilePermission.PermissionLevel permissionLevel = FilePermission.PermissionLevel.valueOf(permission);
        permissionService.shareFile(fileId, targetUser.getId(), permissionLevel);

        // Send email notification
        emailService.sendShareNotification(
                targetEmail,
                sharer.getFullName(),
                fileItem.getName(),
                permission
        );
    }

    /**
     * Lấy danh sách file/thư mục được chia sẻ cho người dùng
     * 
     * @param userId ID của người dùng
     * @return Danh sách file/thư mục được chia sẻ
     */
    public List<FileItemResponse> getSharedWithMe(Long userId) {
        List<FileItem> sharedFiles = fileItemRepository.findSharedWithUser(userId);
        return sharedFiles.stream()
                .map(fileService::mapToResponse)
                .collect(Collectors.toList());
    }
}

