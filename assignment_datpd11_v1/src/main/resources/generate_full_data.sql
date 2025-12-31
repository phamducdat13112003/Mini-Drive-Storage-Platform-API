-- =====================================================
-- Script để generate đầy đủ ~10,000 files
-- =====================================================
-- Sử dụng stored procedure để tạo dữ liệu tự động
-- Chạy script này sau khi đã chạy data.sql
-- =====================================================

DELIMITER $$

-- Procedure để tạo files cho một user
DROP PROCEDURE IF EXISTS generate_user_files$$
CREATE PROCEDURE generate_user_files(
    IN p_user_id INT,
    IN p_start_file_id INT,
    IN p_files_per_folder INT
)
BEGIN
    DECLARE v_root_id INT;
    DECLARE v_folder_id INT;
    DECLARE v_file_id INT;
    DECLARE v_folder_index INT DEFAULT 0;
    DECLARE v_file_index INT DEFAULT 0;
    DECLARE v_file_size BIGINT;
    DECLARE v_user_email VARCHAR(255);
    
    -- Lấy email của user
    SELECT email INTO v_user_email FROM users WHERE id = p_user_id;
    
    -- Tạo root folder
    INSERT INTO file_items (name, type, parent_id, owner_id, file_path, file_size, mime_type, is_deleted, created_at, updated_at)
    VALUES (CONCAT('Root_', v_user_email), 'FOLDER', NULL, p_user_id, NULL, NULL, NULL, 0, NOW(), NOW());
    
    SET v_root_id = LAST_INSERT_ID();
    SET v_file_id = p_start_file_id;
    
    -- Tạo 10 subfolders
    WHILE v_folder_index < 10 DO
        -- Tạo folder
        INSERT INTO file_items (name, type, parent_id, owner_id, file_path, file_size, mime_type, is_deleted, created_at, updated_at)
        VALUES (CONCAT('Folder_', v_folder_index), 'FOLDER', v_root_id, p_user_id, NULL, NULL, NULL, 0, NOW(), NOW());
        
        SET v_folder_id = LAST_INSERT_ID();
        SET v_file_index = 0;
        
        -- Tạo files trong folder
        WHILE v_file_index < p_files_per_folder DO
            SET v_file_size = FLOOR(1000 + RAND() * 999000); -- Random size 1KB - 1MB
            
            INSERT INTO file_items (name, type, parent_id, owner_id, file_path, file_size, mime_type, is_deleted, created_at, updated_at)
            VALUES (
                CONCAT('file_', v_folder_index, '_', v_file_index, '.txt'),
                'FILE',
                v_folder_id,
                p_user_id,
                CONCAT('/mock/path/', p_user_id, '/file_', v_folder_index, '_', v_file_index, '.txt'),
                v_file_size,
                'text/plain',
                0,
                NOW(),
                NOW()
            );
            
            SET v_file_index = v_file_index + 1;
        END WHILE;
        
        SET v_folder_index = v_folder_index + 1;
    END WHILE;
END$$

-- Procedure để generate permissions (10% files)
DROP PROCEDURE IF EXISTS generate_permissions$$
CREATE PROCEDURE generate_permissions()
BEGIN
    DECLARE v_file_id INT;
    DECLARE v_owner_id INT;
    DECLARE v_target_user_id INT;
    DECLARE v_permission VARCHAR(10);
    DECLARE v_count INT DEFAULT 0;
    DECLARE v_total_files INT;
    DECLARE v_files_to_share INT;
    DECLARE done INT DEFAULT 0;

    -- Lưu ý: Trong MySQL, tất cả DECLARE (cursor/handler) phải đứng trước các câu lệnh thực thi
    -- Cursor để lặp qua files (random order)
    DECLARE file_cursor CURSOR FOR
        SELECT id, owner_id FROM file_items 
        WHERE type = 'FILE' AND is_deleted = 0
        ORDER BY RAND();

    -- Handler kết thúc cursor
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    -- Tính số files cần share (10%)
    SELECT COUNT(*) INTO v_total_files FROM file_items WHERE type = 'FILE' AND is_deleted = 0;
    SET v_files_to_share = FLOOR(v_total_files * 0.1);

    OPEN file_cursor;

    read_loop: LOOP
        FETCH file_cursor INTO v_file_id, v_owner_id;

        IF done OR v_count >= v_files_to_share THEN
            LEAVE read_loop;
        END IF;

        -- Chọn user ngẫu nhiên khác owner
        SELECT id INTO v_target_user_id
        FROM users
        WHERE id != v_owner_id
        ORDER BY RAND()
        LIMIT 1;

        -- Random permission (VIEW hoặc EDIT)
        SET v_permission = IF(RAND() > 0.5, 'VIEW', 'EDIT');

        -- Insert permission (ignore nếu đã tồn tại)
        INSERT IGNORE INTO file_permissions (file_id, user_id, permission_level, created_at, updated_at)
        VALUES (v_file_id, v_target_user_id, v_permission, NOW(), NOW());

        SET v_count = v_count + 1;
    END LOOP;

    CLOSE file_cursor;

    SELECT CONCAT('Generated ', v_count, ' permissions') as result;
END$$

DELIMITER ;

-- =====================================================
-- Sử dụng procedures để generate dữ liệu
-- =====================================================

-- Xóa dữ liệu cũ (optional - uncomment nếu cần)
-- TRUNCATE TABLE file_permissions;
-- TRUNCATE TABLE file_items;
-- DELETE FROM users WHERE id > 0;

-- Generate files cho User 1 (bắt đầu từ ID 1)
CALL generate_user_files(1, 1, 100);

-- Generate files cho User 2 (bắt đầu từ ID 1012)
CALL generate_user_files(2, 1012, 100);

-- Generate files cho User 3 (bắt đầu từ ID 2023)
CALL generate_user_files(3, 2023, 100);

-- Generate files cho User 4 (bắt đầu từ ID 3034)
CALL generate_user_files(4, 3034, 100);

-- Generate files cho User 5 (bắt đầu từ ID 4045)
CALL generate_user_files(5, 4045, 100);

-- Generate files cho User 6 (bắt đầu từ ID 5056)
CALL generate_user_files(6, 5056, 100);

-- Generate files cho User 7 (bắt đầu từ ID 6067)
CALL generate_user_files(7, 6067, 100);

-- Generate files cho User 8 (bắt đầu từ ID 7078)
CALL generate_user_files(8, 7078, 100);

-- Generate files cho User 9 (bắt đầu từ ID 8089)
CALL generate_user_files(9, 8089, 100);

-- Generate files cho User 10 (bắt đầu từ ID 9100)
CALL generate_user_files(10, 9100, 100);

-- Generate permissions (10% files)
CALL generate_permissions();

-- =====================================================
-- Verification
-- =====================================================
SELECT 
    'Total Users' as metric,
    COUNT(*) as count
FROM users
UNION ALL
SELECT 
    'Total Files' as metric,
    COUNT(*) as count
FROM file_items 
WHERE type = 'FILE' AND is_deleted = 0
UNION ALL
SELECT 
    'Total Folders' as metric,
    COUNT(*) as count
FROM file_items 
WHERE type = 'FOLDER' AND is_deleted = 0
UNION ALL
SELECT 
    'Total Permissions' as metric,
    COUNT(*) as count
FROM file_permissions;

-- Files per user
SELECT 
    u.email,
    COUNT(f.id) as total_items,
    SUM(CASE WHEN f.type = 'FILE' THEN 1 ELSE 0 END) as files,
    SUM(CASE WHEN f.type = 'FOLDER' THEN 1 ELSE 0 END) as folders
FROM users u
LEFT JOIN file_items f ON u.id = f.owner_id AND f.is_deleted = 0
GROUP BY u.id, u.email
ORDER BY u.id;

-- =====================================================
-- Cleanup procedures
-- =====================================================
DROP PROCEDURE IF EXISTS generate_user_files;
DROP PROCEDURE IF EXISTS generate_permissions;

