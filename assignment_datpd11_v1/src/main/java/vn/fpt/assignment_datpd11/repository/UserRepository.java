package vn.fpt.assignment_datpd11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpt.assignment_datpd11.entity.User;

import java.util.Optional;

/**
 * Repository interface cho entity User
 * 
 * Cung cấp các phương thức truy vấn database cho người dùng
 * Kế thừa từ JpaRepository để có sẵn các phương thức CRUD cơ bản
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Tìm kiếm người dùng theo email
     * 
     * @param email email của người dùng cần tìm
     * @return Optional chứa User nếu tìm thấy, empty nếu không tìm thấy
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Kiểm tra xem email đã tồn tại trong hệ thống chưa
     * 
     * @param email email cần kiểm tra
     * @return true nếu email đã tồn tại, false nếu chưa
     */
    boolean existsByEmail(String email);
}

