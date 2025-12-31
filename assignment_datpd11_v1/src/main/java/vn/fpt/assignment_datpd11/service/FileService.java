package vn.fpt.assignment_datpd11.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpt.assignment_datpd11.dto.response.FileItemResponse;
import vn.fpt.assignment_datpd11.entity.FileItem;
import vn.fpt.assignment_datpd11.entity.User;
import vn.fpt.assignment_datpd11.repository.FileItemRepository;
import vn.fpt.assignment_datpd11.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý các nghiệp vụ liên quan đến file và thư mục
 * 
 * Cung cấp các chức năng:
 * - Upload file
 * - Tạo thư mục
 * - Tìm kiếm file
 * - Xóa file (soft delete)
 * - Chuyển đổi entity sang DTO response
 */
@Service
public class FileService {

    @Autowired
    private FileItemRepository fileItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PermissionService permissionService;

    /**
     * Upload một hoặc nhiều file vào hệ thống
     * 
     * @param files Mảng các file cần upload
     * @param parentId ID của thư mục cha (có thể null để upload vào root)
     * @param userId ID của người dùng upload
     * @return Danh sách thông tin các file đã upload thành công
     * @throws IOException nếu có lỗi khi lưu file
     * @throws RuntimeException nếu người dùng không tồn tại, thư mục cha không tồn tại hoặc không có quyền truy cập
     */
    @Transactional
    public List<FileItemResponse> uploadFiles(MultipartFile[] files, String parentId, Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FileItem parent = null;
        if (parentId != null && !parentId.isEmpty()) {
            parent = fileItemRepository.findByIdAndIsDeletedFalse(Long.parseLong(parentId))
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
            if (!permissionService.hasAccess(userId, parent.getId(), true)) {
                throw new RuntimeException("No permission to access parent folder");
            }
        }

        List<FileItemResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            String filePath = fileStorageService.saveFile(file, userId);
            
            FileItem fileItem = FileItem.builder()
                    .name(file.getOriginalFilename())
                    .type(FileItem.FileType.FILE)
                    .filePath(filePath)
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .parent(parent)
                    .owner(user)
                    .isDeleted(false)
                    .build();

            fileItem = fileItemRepository.save(fileItem);
            responses.add(mapToResponse(fileItem));
        }

        return responses;
    }

    /**
     * Tạo một thư mục mới
     * 
     * @param name Tên của thư mục
     * @param parentId ID của thư mục cha (có thể null để tạo ở root)
     * @param userId ID của người dùng tạo thư mục
     * @return Thông tin thư mục đã tạo
     * @throws RuntimeException nếu người dùng không tồn tại, thư mục cha không tồn tại hoặc không có quyền truy cập
     */
    @Transactional
    public FileItemResponse createFolder(String name, String parentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FileItem parent = null;
        if (parentId != null && !parentId.isEmpty()) {
            parent = fileItemRepository.findByIdAndIsDeletedFalse(Long.parseLong(parentId))
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
            if (!permissionService.hasAccess(userId, parent.getId(), true)) {
                throw new RuntimeException("No permission to access parent folder");
            }
        }

        FileItem folder = FileItem.builder()
                .name(name)
                .type(FileItem.FileType.FOLDER)
                .parent(parent)
                .owner(user)
                .isDeleted(false)
                .build();

        folder = fileItemRepository.save(folder);
        return mapToResponse(folder);
    }

    /**
     * Tìm kiếm file và thư mục với nhiều điều kiện lọc
     * 
     * Tìm kiếm trong:
     * - File/thư mục thuộc sở hữu của người dùng
     * - File/thư mục được chia sẻ cho người dùng
     * 
     * @param userId ID của người dùng
     * @param q Từ khóa tìm kiếm trong tên (có thể null)
     * @param type Loại: "FILE", "FOLDER", hoặc MIME type cụ thể (ví dụ: "image/png", "application/pdf") (có thể null)
     * @param parentId ID của thư mục cha (có thể null)
     * @param fromSize Kích thước tối thiểu (bytes, có thể null)
     * @param toSize Kích thước tối đa (bytes, có thể null)
     * @return Danh sách file/thư mục thỏa mãn điều kiện
     */
    public List<FileItemResponse> searchFiles(Long userId, String q, String type, String parentId, 
                                             Long fromSize, Long toSize) {
        List<FileItem> files = new ArrayList<>();
        
        // Determine if type is FILE/FOLDER enum or MIME type
        FileItem.FileType fileType = null;
        String mimeType = null;
        if (type != null && !type.isEmpty()) {
            try {
                fileType = FileItem.FileType.valueOf(type);
            } catch (IllegalArgumentException e) {
                // Not FILE or FOLDER, treat as MIME type
                mimeType = type;
            }
        }
        
        final FileItem.FileType finalFileType = fileType;
        final String finalMimeType = mimeType;
        
        Long parentIdLong = null;
        if (parentId != null && !parentId.isEmpty()) {
            try {
                parentIdLong = Long.parseLong(parentId);
            } catch (NumberFormatException e) {
                // Invalid parentId, ignore
            }
        }
        
        // Normalize q - if empty string, treat as null
        String searchQuery = (q != null && q.isEmpty()) ? null : q;
        
        List<FileItem> ownedFiles = fileItemRepository.searchFiles(userId, parentIdLong, searchQuery, finalFileType);
        files.addAll(ownedFiles);
        
        // Search in shared files
        List<FileItem> sharedFiles = fileItemRepository.findSharedWithUser(userId);
        if (q != null && !q.isEmpty()) {
            sharedFiles = sharedFiles.stream()
                    .filter(f -> f.getName().contains(q))
                    .collect(Collectors.toList());
        }
        if (finalFileType != null) {
            sharedFiles = sharedFiles.stream()
                    .filter(f -> f.getType() == finalFileType)
                    .collect(Collectors.toList());
        }
        if (finalMimeType != null) {
            sharedFiles = sharedFiles.stream()
                    .filter(f -> f.getMimeType() != null && f.getMimeType().equals(finalMimeType))
                    .collect(Collectors.toList());
        }
        files.addAll(sharedFiles);
        
        // Filter by MIME type for owned files if specified
        if (finalMimeType != null) {
            files = files.stream()
                    .filter(f -> f.getMimeType() != null && f.getMimeType().equals(finalMimeType))
                    .collect(Collectors.toList());
        }
        
        // Filter by size if provided
        if (fromSize != null || toSize != null) {
            long from = fromSize != null ? fromSize : 0;
            long to = toSize != null ? toSize : Long.MAX_VALUE;
            files = files.stream()
                    .filter(f -> f.getFileSize() != null && f.getFileSize() >= from && f.getFileSize() <= to)
                    .collect(Collectors.toList());
        }
        
        return files.stream()
                .map(this::mapToResponse)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Xóa file hoặc thư mục (soft delete)
     * 
     * File/thư mục không bị xóa vật lý khỏi database, chỉ đánh dấu là đã xóa
     * 
     * @param fileId ID của file/thư mục cần xóa
     * @param userId ID của người dùng thực hiện xóa
     * @throws RuntimeException nếu file không tồn tại hoặc người dùng không có quyền xóa
     */
    @Transactional
    public void deleteFile(Long fileId, Long userId) {
        FileItem fileItem = fileItemRepository.findByIdAndIsDeletedFalse(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!permissionService.hasAccess(userId, fileId, true)) {
            throw new RuntimeException("No permission to delete this file");
        }

        fileItem.setIsDeleted(true);
        fileItem.setDeletedAt(LocalDateTime.now());
        fileItemRepository.save(fileItem);
    }

    /**
     * Lấy thông tin file/thư mục theo ID
     * 
     * @param fileId ID của file/thư mục
     * @return FileItem entity
     * @throws RuntimeException nếu file không tồn tại hoặc đã bị xóa
     */
    public FileItem getFileItem(Long fileId) {
        return fileItemRepository.findByIdAndIsDeletedFalse(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
    }

    /**
     * Chuyển đổi FileItem entity sang FileItemResponse DTO
     * 
     * @param fileItem Entity cần chuyển đổi
     * @return FileItemResponse DTO
     */
    public FileItemResponse mapToResponse(FileItem fileItem) {
        return FileItemResponse.builder()
                .id(fileItem.getId())
                .name(fileItem.getName())
                .type(fileItem.getType().name())
                .fileSize(fileItem.getFileSize())
                .mimeType(fileItem.getMimeType())
                .parentId(fileItem.getParent() != null ? fileItem.getParent().getId() : null)
                .ownerEmail(fileItem.getOwner().getEmail())
                .ownerName(fileItem.getOwner().getFullName())
                .createdAt(fileItem.getCreatedAt())
                .updatedAt(fileItem.getUpdatedAt())
                .build();
    }
}

