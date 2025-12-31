package vn.fpt.assignment_datpd11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpt.assignment_datpd11.entity.DownloadRequest;

import java.util.Optional;

/**
 * Repository interface cho entity DownloadRequest
 * 
 * Cung cấp các phương thức truy vấn database cho yêu cầu tải xuống
 * Kế thừa từ JpaRepository với key là String (UUID requestId)
 */
@Repository
public interface DownloadRequestRepository extends JpaRepository<DownloadRequest, String> {
    /**
     * Tìm yêu cầu tải xuống theo requestId (UUID)
     * 
     * @param requestId ID duy nhất của yêu cầu tải xuống
     * @return Optional chứa DownloadRequest nếu tìm thấy, empty nếu không
     */
    Optional<DownloadRequest> findByRequestId(String requestId);
}

