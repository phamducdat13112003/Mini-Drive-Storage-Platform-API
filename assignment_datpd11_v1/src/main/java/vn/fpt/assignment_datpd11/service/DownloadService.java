package vn.fpt.assignment_datpd11.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpt.assignment_datpd11.dto.response.DownloadResponse;
import vn.fpt.assignment_datpd11.entity.DownloadRequest;
import vn.fpt.assignment_datpd11.entity.FileItem;
import vn.fpt.assignment_datpd11.entity.User;
import vn.fpt.assignment_datpd11.repository.DownloadRequestRepository;
import vn.fpt.assignment_datpd11.repository.FileItemRepository;
import vn.fpt.assignment_datpd11.repository.UserRepository;

import java.io.File;

/**
 * Service xử lý các nghiệp vụ liên quan đến tải xuống file/thư mục
 * 
 * Cung cấp các chức năng:
 * - Khởi tạo yêu cầu tải xuống thư mục (tạo file zip bất đồng bộ)
 * - Kiểm tra trạng thái yêu cầu tải xuống
 * - Lấy file zip đã tạo
 */
@Service
public class DownloadService {

    @Autowired
    private DownloadRequestRepository downloadRequestRepository;

    @Autowired
    private FileItemRepository fileItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AsyncDownloadService asyncDownloadService;

    /**
     * Khởi tạo yêu cầu tải xuống thư mục
     * 
     * Quy trình:
     * 1. Kiểm tra thư mục tồn tại và người dùng có quyền truy cập
     * 2. Tạo DownloadRequest với trạng thái PENDING
     * 3. Bắt đầu xử lý bất đồng bộ (tạo file zip)
     * 4. Trả về requestId để client có thể theo dõi trạng thái
     * 
     * @param folderId ID của thư mục cần tải xuống
     * @param userId ID của người dùng yêu cầu tải xuống
     * @return DownloadResponse chứa requestId và trạng thái
     * @throws RuntimeException nếu thư mục không tồn tại, không phải thư mục hoặc không có quyền truy cập
     */
    @Transactional
    public DownloadResponse initiateFolderDownload(Long folderId, Long userId) {
        FileItem folder = fileItemRepository.findByIdAndIsDeletedFalse(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (folder.getType() != FileItem.FileType.FOLDER) {
            throw new RuntimeException("Item is not a folder");
        }

        if (!permissionService.hasAccess(userId, folderId, false)) {
            throw new RuntimeException("No permission to download this folder");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DownloadRequest request = DownloadRequest.builder()
                .fileItem(folder)
                .user(user)
                .status(DownloadRequest.DownloadStatus.PENDING)
                .build();

        request = downloadRequestRepository.save(request);

        // Start async processing
        asyncDownloadService.processFolderDownload(request.getRequestId());

        return DownloadResponse.builder()
                .requestId(request.getRequestId())
                .status(request.getStatus().name())
                .build();
    }

    /**
     * Lấy trạng thái của yêu cầu tải xuống
     * 
     * @param requestId ID của yêu cầu tải xuống
     * @return DownloadResponse chứa trạng thái và downloadUrl (nếu đã sẵn sàng)
     * @throws RuntimeException nếu yêu cầu không tồn tại
     */
    public DownloadResponse getDownloadStatus(String requestId) {
        DownloadRequest request = downloadRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Download request not found"));

        String downloadUrl = null;
        if (request.getStatus() == DownloadRequest.DownloadStatus.READY) {
            downloadUrl = "/api/v1/files/downloads/" + requestId + "/file";
        }

        return DownloadResponse.builder()
                .requestId(request.getRequestId())
                .status(request.getStatus().name())
                .downloadUrl(downloadUrl)
                .errorMessage(request.getErrorMessage())
                .build();
    }

    /**
     * Lấy file zip đã được tạo để tải xuống
     * 
     * @param requestId ID của yêu cầu tải xuống
     * @return File object trỏ đến file zip
     * @throws RuntimeException nếu yêu cầu không tồn tại hoặc chưa sẵn sàng
     */
    public File getDownloadFile(String requestId) {
        DownloadRequest request = downloadRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Download request not found"));

        if (request.getStatus() != DownloadRequest.DownloadStatus.READY) {
            throw new RuntimeException("Download is not ready");
        }

        return new File(request.getZipFilePath());
    }
}

