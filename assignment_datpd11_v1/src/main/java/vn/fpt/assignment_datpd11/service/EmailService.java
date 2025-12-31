package vn.fpt.assignment_datpd11.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service xử lý gửi email
 * 
 * Cung cấp các chức năng:
 * - Gửi email thông báo khi file được chia sẻ
 * - Hỗ trợ mock mode (in ra console) khi không cấu hình mail server
 */
@Service
public class EmailService {

    /**
     * JavaMailSender để gửi email (optional, có thể null nếu mock mode)
     */
    @Autowired(required = false)
    private JavaMailSender mailSender;

    /**
     * Cờ bật/tắt mock mode (mặc định là true)
     * Khi mock mode = true, email sẽ được in ra console thay vì gửi thật
     */
    @Value("${spring.mail.mock.enabled:true}")
    private boolean mockEnabled;

    /**
     * Gửi email thông báo khi file được chia sẻ
     * 
     * Nếu mock mode được bật hoặc mailSender không có, sẽ in email ra console
     * 
     * @param recipientEmail Email của người nhận
     * @param sharerName Tên người chia sẻ
     * @param fileName Tên file được chia sẻ
     * @param permission Mức độ quyền (VIEW hoặc EDIT)
     */
    public void sendShareNotification(String recipientEmail, String sharerName, String fileName, String permission) {
        String subject = "File Shared: " + fileName;
        String message = String.format(
            "Hello,\n\n" +
            "%s has shared a file/folder '%s' with you.\n" +
            "Permission: %s\n\n" +
            "Please log in to your account to access it.\n\n" +
            "Best regards,\n" +
            "Mini-Drive Platform",
            sharerName, fileName, permission
        );

        if (mockEnabled || mailSender == null) {
            // Mock email - just log
            System.out.println("=== MOCK EMAIL ===");
            System.out.println("To: " + recipientEmail);
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("=================");
        } else {
            try {
                SimpleMailMessage email = new SimpleMailMessage();
                email.setTo(recipientEmail);
                email.setSubject(subject);
                email.setText(message);
                mailSender.send(email);
            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
                // Fallback to mock
                System.out.println("=== MOCK EMAIL (Fallback) ===");
                System.out.println("To: " + recipientEmail);
                System.out.println("Subject: " + subject);
                System.out.println("Message: " + message);
            }
        }
    }
}

