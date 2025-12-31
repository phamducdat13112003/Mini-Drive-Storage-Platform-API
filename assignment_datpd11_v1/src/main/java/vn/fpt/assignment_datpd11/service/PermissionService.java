package vn.fpt.assignment_datpd11.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.fpt.assignment_datpd11.entity.FileItem;
import vn.fpt.assignment_datpd11.entity.FilePermission;
import vn.fpt.assignment_datpd11.entity.User;
import vn.fpt.assignment_datpd11.repository.FileItemRepository;
import vn.fpt.assignment_datpd11.repository.FilePermissionRepository;
import vn.fpt.assignment_datpd11.repository.UserRepository;

import java.util.List;

/**
 * Service xử lý các nghiệp vụ liên quan đến quyền truy cập file
 * 
 * Cung cấp các chức năng:
 * - Kiểm tra quyền truy cập của người dùng đối với file
 * - Chia sẻ file/thư mục cho người dùng khác
 * - Chia sẻ đệ quy cho thư mục (bao gồm tất cả file con)
 */
@Service
public class PermissionService {

    @Autowired
    private FileItemRepository fileItemRepository;

    @Autowired
    private FilePermissionRepository filePermissionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Kiểm tra người dùng có quyền truy cập file/thư mục không
     * 
     * Quy tắc:
     * - Người sở hữu luôn có quyền truy cập đầy đủ
     * - Người được chia sẻ có quyền tùy theo mức độ (VIEW hoặc EDIT)
     * 
     * @param userId ID của người dùng cần kiểm tra
     * @param fileId ID của file/thư mục
     * @param requireEdit true nếu cần quyền EDIT, false nếu chỉ cần VIEW
     * @return true nếu có quyền truy cập, false nếu không
     * @throws RuntimeException nếu file không tồn tại
     */
    public boolean hasAccess(Long userId, Long fileId, boolean requireEdit) {
        FileItem fileItem = fileItemRepository.findByIdAndIsDeletedFalse(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Owner has full access
        if (fileItem.getOwner().getId().equals(userId)) {
            return true;
        }

        // Check permissions
        FilePermission permission = filePermissionRepository
                .findByFileItemIdAndUserId(fileId, userId)
                .orElse(null);

        if (permission == null) {
            return false;
        }

        if (requireEdit) {
            return permission.getPermissionLevel() == FilePermission.PermissionLevel.EDIT;
        }

        return true;
    }

    /**
     * Chia sẻ file/thư mục cho một người dùng
     * 
     * Nếu là thư mục, sẽ chia sẻ đệ quy cho tất cả file và thư mục con
     * Nếu quyền đã tồn tại, sẽ cập nhật mức độ quyền
     * 
     * @param fileId ID của file/thư mục cần chia sẻ
     * @param targetUserId ID của người dùng được chia sẻ
     * @param permissionLevel Mức độ quyền: VIEW hoặc EDIT
     * @throws RuntimeException nếu file hoặc người dùng không tồn tại
     */
    public void shareFile(Long fileId, Long targetUserId, FilePermission.PermissionLevel permissionLevel) {
        FileItem fileItem = fileItemRepository.findByIdAndIsDeletedFalse(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // If it's a folder, apply recursively
        if (fileItem.getType() == FileItem.FileType.FOLDER) {
            shareFolderRecursive(fileItem, targetUserId, permissionLevel);
        } else {
            shareSingleFile(fileItem, targetUserId, permissionLevel);
        }
    }

    /**
     * Chia sẻ một file đơn lẻ cho người dùng
     * 
     * @param fileItem File cần chia sẻ
     * @param targetUserId ID của người dùng được chia sẻ
     * @param permissionLevel Mức độ quyền
     */
    private void shareSingleFile(FileItem fileItem, Long targetUserId, FilePermission.PermissionLevel permissionLevel) {
        FilePermission existingPermission = filePermissionRepository
                .findByFileItemIdAndUserId(fileItem.getId(), targetUserId)
                .orElse(null);

        if (existingPermission != null) {
            existingPermission.setPermissionLevel(permissionLevel);
            filePermissionRepository.save(existingPermission);
        } else {
            User targetUser = userRepository.findById(targetUserId)
                    .orElseThrow(() -> new RuntimeException("Target user not found"));

            FilePermission permission = FilePermission.builder()
                    .fileItem(fileItem)
                    .user(targetUser)
                    .permissionLevel(permissionLevel)
                    .build();

            filePermissionRepository.save(permission);
        }
    }

    /**
     * Chia sẻ thư mục đệ quy cho người dùng
     * 
     * Chia sẻ thư mục và tất cả file/thư mục con bên trong
     * 
     * @param folder Thư mục cần chia sẻ
     * @param targetUserId ID của người dùng được chia sẻ
     * @param permissionLevel Mức độ quyền
     */
    private void shareFolderRecursive(FileItem folder, Long targetUserId, FilePermission.PermissionLevel permissionLevel) {
        shareSingleFile(folder, targetUserId, permissionLevel);

        List<FileItem> children = fileItemRepository.findByParentIdAndIsDeletedFalse(folder.getId());
        for (FileItem child : children) {
            if (child.getType() == FileItem.FileType.FOLDER) {
                shareFolderRecursive(child, targetUserId, permissionLevel);
            } else {
                shareSingleFile(child, targetUserId, permissionLevel);
            }
        }
    }
}

