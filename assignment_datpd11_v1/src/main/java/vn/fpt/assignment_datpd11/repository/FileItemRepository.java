package vn.fpt.assignment_datpd11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpt.assignment_datpd11.entity.FileItem;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity FileItem
 * 
 * Cung cấp các phương thức truy vấn database cho file và thư mục
 * Kế thừa từ JpaRepository để có sẵn các phương thức CRUD cơ bản
 */
@Repository
public interface FileItemRepository extends JpaRepository<FileItem, Long> {
    /**
     * Tìm tất cả file/thư mục con của một thư mục cha (chưa bị xóa)
     * 
     * @param parentId ID của thư mục cha
     * @return Danh sách file/thư mục con
     */
    List<FileItem> findByParentIdAndIsDeletedFalse(Long parentId);
    
    /**
     * Tìm tất cả file/thư mục thuộc sở hữu của một người dùng (chưa bị xóa)
     * 
     * @param ownerId ID của người sở hữu
     * @return Danh sách file/thư mục
     */
    List<FileItem> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    /**
     * Tìm file/thư mục theo ID (chưa bị xóa)
     * 
     * @param id ID của file/thư mục
     * @return Optional chứa FileItem nếu tìm thấy, empty nếu không tìm thấy hoặc đã bị xóa
     */
    Optional<FileItem> findByIdAndIsDeletedFalse(Long id);
    
    /**
     * Tìm kiếm file/thư mục với nhiều điều kiện lọc
     * 
     * @param userId ID của người sở hữu
     * @param parentId ID của thư mục cha (có thể null để tìm ở root)
     * @param q Từ khóa tìm kiếm trong tên (có thể null hoặc rỗng)
     * @param type Loại file: FILE hoặc FOLDER (có thể null)
     * @return Danh sách file/thư mục thỏa mãn điều kiện
     */
    @Query("SELECT f FROM FileItem f WHERE f.owner.id = :userId AND f.isDeleted = false " +
           "AND (:parentId IS NULL OR f.parent.id = :parentId) " +
           "AND (:q IS NULL OR :q = '' OR f.name LIKE CONCAT('%', :q, '%')) " +
           "AND (:type IS NULL OR f.type = :type)")
    List<FileItem> searchFiles(@Param("userId") Long userId, 
                                @Param("parentId") Long parentId,
                                @Param("q") String q,
                                @Param("type") FileItem.FileType type);
    
    /**
     * Tìm file theo khoảng kích thước
     * 
     * @param userId ID của người sở hữu
     * @param fromSize Kích thước tối thiểu (bytes)
     * @param toSize Kích thước tối đa (bytes)
     * @return Danh sách file thỏa mãn điều kiện kích thước
     */
    @Query("SELECT f FROM FileItem f WHERE f.owner.id = :userId AND f.isDeleted = false " +
           "AND f.fileSize >= :fromSize AND f.fileSize <= :toSize")
    List<FileItem> findBySizeRange(@Param("userId") Long userId,
                                    @Param("fromSize") Long fromSize,
                                    @Param("toSize") Long toSize);
    
    /**
     * Tìm tất cả file/thư mục được chia sẻ cho một người dùng
     * 
     * @param userId ID của người dùng
     * @return Danh sách file/thư mục được chia sẻ (chưa bị xóa)
     */
    @Query("SELECT f FROM FileItem f JOIN f.permissions p WHERE p.user.id = :userId " +
           "AND f.isDeleted = false")
    List<FileItem> findSharedWithUser(@Param("userId") Long userId);
    
    /**
     * Tìm tất cả file/thư mục đã bị xóa trước một ngày cụ thể
     * Dùng để cleanup các file đã xóa quá lâu
     * 
     * @param cutoffDate Ngày cắt (chỉ lấy file xóa trước ngày này)
     * @return Danh sách file/thư mục đã bị xóa
     */
    @Query("SELECT f FROM FileItem f WHERE f.isDeleted = true " +
           "AND f.deletedAt < :cutoffDate")
    List<FileItem> findDeletedBefore(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
    
    /**
     * Tính tổng dung lượng storage của một người dùng
     * 
     * @param userId ID của người dùng
     * @return Tổng dung lượng tính bằng bytes (0 nếu không có file nào)
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileItem f WHERE f.owner.id = :userId " +
           "AND f.isDeleted = false AND f.type = 'FILE'")
    Long getTotalSizeByOwner(@Param("userId") Long userId);
}

