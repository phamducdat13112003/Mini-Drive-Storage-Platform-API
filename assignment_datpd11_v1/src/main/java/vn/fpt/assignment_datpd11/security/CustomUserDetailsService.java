package vn.fpt.assignment_datpd11.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.fpt.assignment_datpd11.entity.User;
import vn.fpt.assignment_datpd11.repository.UserRepository;

/**
 * Service tùy chỉnh để load thông tin người dùng cho Spring Security
 * 
 * Implement UserDetailsService để Spring Security có thể xác thực người dùng
 * Sử dụng email làm username
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Load thông tin người dùng từ database theo email
     * 
     * @param email Email của người dùng (được sử dụng làm username)
     * @return UserDetails object chứa thông tin người dùng cho Spring Security
     * @throws UsernameNotFoundException nếu không tìm thấy người dùng
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}

