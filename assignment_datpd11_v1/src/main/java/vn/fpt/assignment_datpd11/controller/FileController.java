package vn.fpt.assignment_datpd11.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.fpt.assignment_datpd11.dto.request.CreateFolderRequest;
import vn.fpt.assignment_datpd11.dto.response.ApiResponse;
import vn.fpt.assignment_datpd11.dto.response.DownloadResponse;
import vn.fpt.assignment_datpd11.dto.response.FileItemResponse;
import vn.fpt.assignment_datpd11.entity.FileItem;
import vn.fpt.assignment_datpd11.repository.UserRepository;
import vn.fpt.assignment_datpd11.service.DownloadService;
import vn.fpt.assignment_datpd11.service.FileService;
import vn.fpt.assignment_datpd11.service.PermissionService;

import java.io.File;
import java.util.List;

/**
 * Controller xử lý các request liên quan đến file và thư mục
 * 
 * Endpoints:
 * - POST /api/v1/files (multipart/form-data) - Upload file
 * - POST /api/v1/files (application/json) - Tạo thư mục
 * - GET /api/v1/files - Tìm kiếm file
 * - GET /api/v1/files/{id}/download - Tải xuống file/thư mục
 * - POST /api/v1/files/{id}/download - Khởi tạo tải xuống thư mục
 * - GET /api/v1/files/downloads/{requestId} - Kiểm tra trạng thái tải xuống
 * - GET /api/v1/files/downloads/{requestId}/file - Tải file zip
 * - DELETE /api/v1/files/{id} - Xóa file/thư mục
 */
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private PermissionService permissionService;

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
     * Upload một hoặc nhiều file
     * 
     * @param files Mảng các file cần upload
     * @param parentId ID của thư mục cha (tùy chọn)
     * @param authentication Authentication object từ Spring Security
     * @return ResponseEntity chứa danh sách file đã upload thành công
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<FileItemResponse>>> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "parentId", required = false) String parentId,
            Authentication authentication) {
        try {
            Long userId = getUserId(authentication);
            List<FileItemResponse> responses = fileService.uploadFiles(files, parentId, userId);
            return ResponseEntity.ok(ApiResponse.success("Files uploaded successfully", responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Tạo thư mục mới
     * 
     * @param request Thông tin thư mục (name, parentId)
     * @param authentication Authentication object từ Spring Security
     * @return ResponseEntity chứa thông tin thư mục đã tạo
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FileItemResponse>> createFolder(
            @Valid @RequestBody CreateFolderRequest request,
            Authentication authentication) {
        try {
            Long userId = getUserId(authentication);
            FileItemResponse response = fileService.createFolder(request.getName(), request.getParentId(), userId);
            return ResponseEntity.ok(ApiResponse.success("Folder created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Tìm kiếm file và thư mục với nhiều điều kiện lọc
     * 
     * @param q Từ khóa tìm kiếm trong tên (tùy chọn)
     * @param type Loại: "FILE" hoặc "FOLDER" (tùy chọn)
     * @param parentId ID của thư mục cha (tùy chọn)
     * @param fromSize Kích thước tối thiểu (bytes, tùy chọn)
     * @param toSize Kích thước tối đa (bytes, tùy chọn)
     * @param authentication Authentication object từ Spring Security
     * @return ResponseEntity chứa danh sách file/thư mục thỏa mãn điều kiện
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<FileItemResponse>>> searchFiles(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "parentId", required = false) String parentId,
            @RequestParam(value = "fromSize", required = false) Long fromSize,
            @RequestParam(value = "toSize", required = false) Long toSize,
            Authentication authentication) {
        try {
            Long userId = getUserId(authentication);
            List<FileItemResponse> files = fileService.searchFiles(userId, q, type, parentId, fromSize, toSize);
            return ResponseEntity.ok(ApiResponse.success(files));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Tải xuống file hoặc thư mục
     * 
     * - Nếu là file: trả về file trực tiếp
     * - Nếu là thư mục: khởi tạo yêu cầu tải xuống bất đồng bộ và trả về requestId
     * 
     * @param id ID của file/thư mục cần tải xuống
     * @param authentication Authentication object từ Spring Security
     * @return ResponseEntity chứa file hoặc DownloadResponse với requestId
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> downloadFile(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = getUserId(authentication);
            FileItem fileItem = fileService.getFileItem(id);

            if (!permissionService.hasAccess(userId, id, false)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("No permission to download this file"));
            }

            if (fileItem.getType() == FileItem.FileType.FILE) {
                // Direct file download
                File file = new File(fileItem.getFilePath());
                if (!file.exists()) {
                    return ResponseEntity.notFound().build();
                }

                Resource resource = new FileSystemResource(file);
                String contentType = fileItem.getMimeType() != null 
                    ? fileItem.getMimeType() 
                    : "application/octet-stream";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + fileItem.getName() + "\"")
                        .body(resource);
            } else {
                // Folder download - initiate async
                DownloadResponse response = downloadService.initiateFolderDownload(id, userId);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ApiResponse.success(response));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Khởi tạo yêu cầu tải xuống thư mục (tạo file zip bất đồng bộ)
     * 
     * @param id ID của thư mục cần tải xuống
     * @param authentication Authentication object từ Spring Security
     * @return ResponseEntity chứa DownloadResponse với requestId
     */
    @PostMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DownloadResponse>> initiateFolderDownload(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = getUserId(authentication);
            DownloadResponse response = downloadService.initiateFolderDownload(id, userId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Kiểm tra trạng thái của yêu cầu tải xuống
     * 
     * @param requestId ID của yêu cầu tải xuống (UUID)
     * @return ResponseEntity chứa DownloadResponse với trạng thái và downloadUrl (nếu sẵn sàng)
     */
    @GetMapping("/downloads/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DownloadResponse>> getDownloadStatus(@PathVariable String requestId) {
        try {
            DownloadResponse response = downloadService.getDownloadStatus(requestId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Tải xuống file zip đã được tạo
     * 
     * @param requestId ID của yêu cầu tải xuống (UUID)
     * @return ResponseEntity chứa file zip
     */
    @GetMapping("/downloads/{requestId}/file")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadZipFile(@PathVariable String requestId) {
        try {
            File zipFile = downloadService.getDownloadFile(requestId);
            Resource resource = new FileSystemResource(zipFile);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + zipFile.getName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Xóa file hoặc thư mục (soft delete)
     * 
     * @param id ID của file/thư mục cần xóa
     * @param authentication Authentication object từ Spring Security
     * @return ResponseEntity chứa thông báo thành công
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = getUserId(authentication);
            fileService.deleteFile(id, userId);
            return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

