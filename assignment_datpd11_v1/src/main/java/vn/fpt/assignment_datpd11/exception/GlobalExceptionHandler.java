package vn.fpt.assignment_datpd11.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import vn.fpt.assignment_datpd11.dto.response.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler để xử lý tất cả các exception trong ứng dụng
 * 
 * Cung cấp xử lý thống nhất cho:
 * - Validation errors (MethodArgumentNotValidException)
 * - Security exceptions (AccessDeniedException, AuthenticationCredentialsNotFoundException)
 * - Runtime exceptions (business logic errors)
 * - File upload exceptions
 * - Generic exceptions
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi validation (từ @Valid annotation)
     * 
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity chứa danh sách lỗi validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .error("Validation failed")
                .data(errors)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Xử lý lỗi truy cập bị từ chối (403 Forbidden)
     * 
     * @param ex AccessDeniedException
     * @return ResponseEntity chứa thông báo lỗi với status 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access denied: " + ex.getMessage()));
    }

    /**
     * Xử lý lỗi xác thực (401 Unauthorized)
     * 
     * @param ex AuthenticationCredentialsNotFoundException
     * @return ResponseEntity chứa thông báo lỗi với status 401
     */
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationCredentialsNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication required: " + ex.getMessage()));
    }

    /**
     * Xử lý lỗi file upload quá lớn
     * 
     * @param ex MaxUploadSizeExceededException
     * @return ResponseEntity chứa thông báo lỗi
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxUploadSizeException(
            MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("File size exceeds maximum allowed size"));
    }

    /**
     * Xử lý RuntimeException (các lỗi nghiệp vụ)
     * 
     * @param ex RuntimeException
     * @return ResponseEntity chứa thông báo lỗi
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        // Check for common business logic errors
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("not found") || message.contains("Not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(message));
            }
            if (message.contains("permission") || message.contains("Permission") || 
                message.contains("access") || message.contains("Access")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(message));
            }
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(message != null ? message : "An error occurred"));
    }

    /**
     * Xử lý các exception chung (catch-all)
     * 
     * @param ex Exception
     * @return ResponseEntity chứa thông báo lỗi với status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + 
                    (ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName())));
    }
}

