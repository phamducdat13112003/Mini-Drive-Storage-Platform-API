package vn.fpt.assignment_datpd11.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Component xử lý JWT token
 * 
 * Cung cấp các chức năng:
 * - Tạo JWT token từ thông tin người dùng
 * - Trích xuất thông tin từ token
 * - Xác thực token
 * - Kiểm tra token hết hạn
 */
@Component
public class JwtTokenProvider {
    
    /**
     * Secret key để ký và xác thực JWT token (đọc từ application.properties)
     */
    @Value("${jwt.secret}")
    private String secret;
    
    /**
     * Thời gian hết hạn của token tính bằng milliseconds (đọc từ application.properties)
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Tạo SecretKey từ secret string
     * 
     * @return SecretKey để ký và xác thực token
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Trích xuất username (email) từ JWT token
     * 
     * @param token JWT token
     * @return Email của người dùng
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất thời gian hết hạn từ JWT token
     * 
     * @param token JWT token
     * @return Date hết hạn của token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Trích xuất một claim cụ thể từ JWT token
     * 
     * @param token JWT token
     * @param claimsResolver Function để trích xuất claim
     * @return Giá trị của claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Trích xuất tất cả claims từ JWT token
     * 
     * @param token JWT token
     * @return Claims object chứa tất cả thông tin trong token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Kiểm tra token đã hết hạn chưa
     * 
     * @param token JWT token
     * @return true nếu token đã hết hạn, false nếu chưa
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Tạo JWT token từ thông tin người dùng
     * 
     * @param userDetails Thông tin người dùng từ Spring Security
     * @return JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Tạo JWT token với claims và subject
     * 
     * @param claims Các claims bổ sung (hiện tại không sử dụng)
     * @param subject Subject của token (email của người dùng)
     * @return JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Xác thực JWT token
     * 
     * Kiểm tra:
     * - Username trong token khớp với username của UserDetails
     * - Token chưa hết hạn
     * 
     * @param token JWT token cần xác thực
     * @param userDetails Thông tin người dùng từ Spring Security
     * @return true nếu token hợp lệ, false nếu không
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

