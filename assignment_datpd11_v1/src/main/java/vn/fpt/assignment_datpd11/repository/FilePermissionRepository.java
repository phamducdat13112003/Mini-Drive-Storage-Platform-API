package vn.fpt.assignment_datpd11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpt.assignment_datpd11.entity.FilePermission;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity FilePermission
 * 
 * Cung cấp các phương thức truy vấn database cho quyền truy cập file
 * Kế thừa từ JpaRepository để có sẵn các phương thức CRUD cơ bản
 */
@Repository
public interface FilePermissionRepository extends JpaRepository<FilePermission, Long> {
    /**
     * Tìm quyền truy cập của một người dùng đối với một file cụ thể
     * 
     * @param fileId ID của file
     * @param userId ID của người dùng
     * @return Optional chứa FilePermission nếu tìm thấy, empty nếu không
     */
    Optional<FilePermission> findByFileItemIdAndUserId(Long fileId, Long userId);
    
    /**
     * Tìm tất cả quyền truy cập của một file
     * 
     * @param fileId ID của file
     * @return Danh sách quyền truy cập
     */
    List<FilePermission> findByFileItemId(Long fileId);
    
    /**
     * Tìm tất cả quyền truy cập của một người dùng
     * 
     * @param userId ID của người dùng
     * @return Danh sách quyền truy cập
     */
    List<FilePermission> findByUserId(Long userId);
    
    /**
     * Tìm tất cả quyền truy cập của một file và các file con (đệ quy)
     * 
     * @param fileId ID của file/thư mục
     * @return Danh sách quyền truy cập bao gồm cả file con
     */
    @Query("SELECT fp FROM FilePermission fp WHERE fp.fileItem.id = :fileId " +
           "OR fp.fileItem.parent.id = :fileId")
    List<FilePermission> findByFileItemIdRecursive(@Param("fileId") Long fileId);
    
    /**
     * Xóa tất cả quyền truy cập của một file
     * 
     * @param fileId ID của file
     */
    void deleteByFileItemId(Long fileId);
}

