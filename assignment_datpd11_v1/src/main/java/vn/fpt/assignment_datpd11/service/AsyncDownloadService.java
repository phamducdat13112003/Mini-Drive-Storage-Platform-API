package vn.fpt.assignment_datpd11.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpt.assignment_datpd11.entity.DownloadRequest;
import vn.fpt.assignment_datpd11.entity.FileItem;
import vn.fpt.assignment_datpd11.repository.DownloadRequestRepository;
import vn.fpt.assignment_datpd11.repository.FileItemRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service xử lý tải xuống thư mục bất đồng bộ
 * 
 * Cung cấp các chức năng:
 * - Tạo file zip từ thư mục và tất cả file con (xử lý bất đồng bộ)
 * - Cập nhật trạng thái yêu cầu tải xuống
 */
@Service
public class AsyncDownloadService {

    @Autowired
    private DownloadRequestRepository downloadRequestRepository;

    @Autowired
    private FileItemRepository fileItemRepository;

    /**
     * Đường dẫn thư mục tạm để lưu file zip (đọc từ application.properties)
     */
    @Value("${file.storage.temp.path}")
    private String tempPath;

    /**
     * Xử lý tải xuống thư mục bất đồng bộ
     * 
     * Quy trình:
     * 1. Cập nhật trạng thái thành PROCESSING
     * 2. Tạo file zip từ thư mục và tất cả file con
     * 3. Lưu đường dẫn file zip
     * 4. Cập nhật trạng thái thành READY
     * 5. Nếu có lỗi, cập nhật trạng thái thành FAILED
     * 
     * @param requestId ID của yêu cầu tải xuống
     */
    @Async
    @Transactional
    public void processFolderDownload(String requestId) {
        DownloadRequest request = downloadRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new RuntimeException("Download request not found"));

        try {
            request.setStatus(DownloadRequest.DownloadStatus.PROCESSING);
            downloadRequestRepository.save(request);

            FileItem folder = request.getFileItem();
            Path zipPath = createZipFile(folder);

            request.setZipFilePath(zipPath.toString());
            request.setStatus(DownloadRequest.DownloadStatus.READY);
            downloadRequestRepository.save(request);
        } catch (Exception e) {
            request.setStatus(DownloadRequest.DownloadStatus.FAILED);
            request.setErrorMessage(e.getMessage());
            downloadRequestRepository.save(request);
        }
    }

    /**
     * Tạo file zip từ thư mục
     * 
     * @param folder Thư mục cần zip
     * @return Đường dẫn đến file zip đã tạo
     * @throws IOException nếu có lỗi khi tạo file zip
     */
    private Path createZipFile(FileItem folder) throws IOException {
        Path tempDir = Paths.get(tempPath);
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }

        String zipFileName = folder.getName() + "_" + UUID.randomUUID() + ".zip";
        Path zipPath = tempDir.resolve(zipFileName);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            addFolderToZip(folder, zos, folder.getName() + "/");
        }

        return zipPath;
    }

    /**
     * Thêm thư mục và tất cả file con vào zip (đệ quy)
     * 
     * @param folder Thư mục cần thêm
     * @param zos ZipOutputStream để ghi file
     * @param basePath Đường dẫn cơ sở trong zip
     * @throws IOException nếu có lỗi khi ghi file
     */
    private void addFolderToZip(FileItem folder, ZipOutputStream zos, String basePath) throws IOException {
        List<FileItem> children = fileItemRepository.findByParentIdAndIsDeletedFalse(folder.getId());
        
        for (FileItem item : children) {
            String entryPath = basePath + item.getName();
            
            if (item.getType() == FileItem.FileType.FOLDER) {
                zos.putNextEntry(new ZipEntry(entryPath + "/"));
                zos.closeEntry();
                addFolderToZip(item, zos, entryPath + "/");
            } else {
                zos.putNextEntry(new ZipEntry(entryPath));
                if (item.getFilePath() != null && Files.exists(Paths.get(item.getFilePath()))) {
                    Files.copy(Paths.get(item.getFilePath()), zos);
                }
                zos.closeEntry();
            }
        }
    }
}

