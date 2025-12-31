package vn.fpt.assignment_datpd11.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.fpt.assignment_datpd11.dto.response.ApiResponse;
import vn.fpt.assignment_datpd11.entity.FileItem;
import vn.fpt.assignment_datpd11.entity.FilePermission;
import vn.fpt.assignment_datpd11.entity.User;
import vn.fpt.assignment_datpd11.repository.FileItemRepository;
import vn.fpt.assignment_datpd11.repository.FilePermissionRepository;
import vn.fpt.assignment_datpd11.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controller dùng cho mục đích debug và testing
 * 
 * Cung cấp các endpoint để:
 * - Tạo dữ liệu mẫu cho testing
 * 
 * Lưu ý: Controller này nên được tắt hoặc bảo vệ trong môi trường production
 */
@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileItemRepository fileItemRepository;

    @Autowired
    private FilePermissionRepository filePermissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Tạo dữ liệu mẫu cho hệ thống (dùng cho testing)
     * 
     * Tạo:
     * - 10 người dùng mẫu
     * - Tổng cộng ~10,000 files toàn hệ thống (mỗi user ~1,000 files)
     * - 10% file được chia sẻ ngẫu nhiên
     * 
     * @return ResponseEntity chứa thông báo kết quả
     */
    @PostMapping("/generate-system")
    @Transactional
    public ResponseEntity<ApiResponse<String>> generateSystem() {
        try {
            // Create 10 users
            List<User> users = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                User user = User.builder()
                        .email("user" + i + "@example.com")
                        .password(passwordEncoder.encode("password123"))
                        .fullName("User " + i)
                        .build();
                users.add(userRepository.save(user));
            }

            // Each user creates ~1000 files (total ~10,000 files)
            // Structure: 1 root folder + 10 subfolders + 100 files per subfolder = 1 + 10 + 1000 = 1011 items per user
            // Total: 10 users * 1011 = 10,110 items (close to 10,000)
            Random random = new Random();
            List<FileItem> allFiles = new ArrayList<>();

            for (User user : users) {
                // Create folder structure
                FileItem rootFolder = FileItem.builder()
                        .name("Root_" + user.getEmail())
                        .type(FileItem.FileType.FOLDER)
                        .owner(user)
                        .isDeleted(false)
                        .build();
                rootFolder = fileItemRepository.save(rootFolder);
                allFiles.add(rootFolder);

                // Create subfolders and files
                for (int i = 0; i < 10; i++) {
                    FileItem subFolder = FileItem.builder()
                            .name("Folder_" + i)
                            .type(FileItem.FileType.FOLDER)
                            .parent(rootFolder)
                            .owner(user)
                            .isDeleted(false)
                            .build();
                    subFolder = fileItemRepository.save(subFolder);
                    allFiles.add(subFolder);

                    // Create files in each subfolder (100 files per subfolder)
                    for (int j = 0; j < 100; j++) {
                        FileItem file = FileItem.builder()
                                .name("file_" + i + "_" + j + ".txt")
                                .type(FileItem.FileType.FILE)
                                .parent(subFolder)
                                .owner(user)
                                .fileSize((long) (random.nextInt(1000000) + 1000))
                                .mimeType("text/plain")
                                .filePath("/mock/path/" + user.getId() + "/file_" + i + "_" + j + ".txt")
                                .isDeleted(false)
                                .build();
                        file = fileItemRepository.save(file);
                        allFiles.add(file);
                    }
                }
            }

            // Share 10% of files randomly
            int shareCount = (int) (allFiles.size() * 0.1);
            for (int i = 0; i < shareCount; i++) {
                FileItem file = allFiles.get(random.nextInt(allFiles.size()));
                User owner = file.getOwner();
                User targetUser = users.get(random.nextInt(users.size()));
                
                if (!targetUser.getId().equals(owner.getId())) {
                    FilePermission.PermissionLevel permission = random.nextBoolean() 
                        ? FilePermission.PermissionLevel.VIEW 
                        : FilePermission.PermissionLevel.EDIT;
                    
                    FilePermission filePermission = FilePermission.builder()
                            .fileItem(file)
                            .user(targetUser)
                            .permissionLevel(permission)
                            .build();
                    filePermissionRepository.save(filePermission);
                }
            }

            return ResponseEntity.ok(ApiResponse.success(
                "Generated system with " + users.size() + " users and " + allFiles.size() + " files/folders (~10,000 files)"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error generating system: " + e.getMessage()));
        }
    }
}

