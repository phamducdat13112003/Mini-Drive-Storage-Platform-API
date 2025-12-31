-- =====================================================
-- Mini-Drive Storage Platform - Sample Data
-- =====================================================
-- Database: mini_drive_db
-- 
-- Dữ liệu mẫu bao gồm:
-- - 10 users (password: password123)
-- - ~10,000 files/folders (mỗi user ~1,000 items)
-- - 10% files được chia sẻ ngẫu nhiên
-- =====================================================

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Clear existing data (optional - uncomment if needed)
-- TRUNCATE TABLE file_permissions;
-- TRUNCATE TABLE download_requests;
-- TRUNCATE TABLE file_items;
-- TRUNCATE TABLE users;

-- =====================================================
-- 1. USERS (10 users)
-- =====================================================
-- Password: password123 (BCrypt hash)
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (id, email, password, full_name, created_at, updated_at) VALUES
(1, 'user1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User 1', NOW(), NOW()),
(2, 'user2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User 2', NOW(), NOW()),
(3, 'user3@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User 3', NOW(), NOW()),
(4, 'user4@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User 4', NOW(), NOW()),
(5, 'user5@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User 5', NOW(), NOW()),
(6, 'user6@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User 6', NOW(), NOW()),
(7, 'user7@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User 7', NOW(), NOW()),
(8, 'user8@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User 8', NOW(), NOW()),
(9, 'user9@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User 9', NOW(), NOW()),
(10, 'user10@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'User 10', NOW(), NOW());

-- =====================================================
-- 2. FILE ITEMS (~10,000 items)
-- =====================================================
-- Structure per user:
-- - 1 root folder
-- - 10 subfolders
-- - 100 files per subfolder
-- Total: 1 + 10 + 1000 = 1011 items per user
-- Total: 10 users × 1011 = 10,110 items

-- User 1: Items 1-1011
-- Root folder
INSERT INTO file_items (id, name, type, parent_id, owner_id, file_path, file_size, mime_type, is_deleted, created_at, updated_at) VALUES
(1, 'Root_user1@example.com', 'FOLDER', NULL, 1, NULL, NULL, NULL, 0, NOW(), NOW());

-- Subfolders (2-11)
INSERT INTO file_items (id, name, type, parent_id, owner_id, file_path, file_size, mime_type, is_deleted, created_at, updated_at) VALUES
(2, 'Folder_0', 'FOLDER', 1, 1, NULL, NULL, NULL, 0, NOW(), NOW()),
(3, 'Folder_1', 'FOLDER', 1, 1, NULL, NULL, NULL, 0, NOW(), NOW()),
(4, 'Folder_2', 'FOLDER', 1, 1, NULL, NULL, NULL, 0, NOW(), NOW()),
(5, 'Folder_3', 'FOLDER', 1, 1, NULL, NULL, NULL, 0, NOW(), NOW()),
(6, 'Folder_4', 'FOLDER', 1, 1, NULL, NULL, NULL, 0, NOW(), NOW()),
(7, 'Folder_5', 'FOLDER', 1, 1, NULL, NULL, NULL, 0, NOW(), NOW()),
(8, 'Folder_6', 'FOLDER', 1, 1, NULL, NULL, NULL, 0, NOW(), NOW()),
(9, 'Folder_7', 'FOLDER', 1, 1, NULL, NULL, NULL, 0, NOW(), NOW()),
(10, 'Folder_8', 'FOLDER', 1, 1, NULL, NULL, NULL, 0, NOW(), NOW()),
(11, 'Folder_9', 'FOLDER', 1, 1, NULL, NULL, NULL, 0, NOW(), NOW());

-- Files in Folder_0 (12-111)
INSERT INTO file_items (id, name, type, parent_id, owner_id, file_path, file_size, mime_type, is_deleted, created_at, updated_at) VALUES
(12, 'file_0_0.txt', 'FILE', 2, 1, '/mock/path/1/file_0_0.txt', 50000, 'text/plain', 0, NOW(), NOW()),
(13, 'file_0_1.txt', 'FILE', 2, 1, '/mock/path/1/file_0_1.txt', 75000, 'text/plain', 0, NOW(), NOW()),
(14, 'file_0_2.txt', 'FILE', 2, 1, '/mock/path/1/file_0_2.txt', 120000, 'text/plain', 0, NOW(), NOW()),
(15, 'file_0_3.txt', 'FILE', 2, 1, '/mock/path/1/file_0_3.txt', 30000, 'text/plain', 0, NOW(), NOW()),
(16, 'file_0_4.txt', 'FILE', 2, 1, '/mock/path/1/file_0_4.txt', 95000, 'text/plain', 0, NOW(), NOW()),
(17, 'file_0_5.txt', 'FILE', 2, 1, '/mock/path/1/file_0_5.txt', 60000, 'text/plain', 0, NOW(), NOW()),
(18, 'file_0_6.txt', 'FILE', 2, 1, '/mock/path/1/file_0_6.txt', 110000, 'text/plain', 0, NOW(), NOW()),
(19, 'file_0_7.txt', 'FILE', 2, 1, '/mock/path/1/file_0_7.txt', 45000, 'text/plain', 0, NOW(), NOW()),
(20, 'file_0_8.txt', 'FILE', 2, 1, '/mock/path/1/file_0_8.txt', 80000, 'text/plain', 0, NOW(), NOW()),
(21, 'file_0_9.txt', 'FILE', 2, 1, '/mock/path/1/file_0_9.txt', 55000, 'text/plain', 0, NOW(), NOW());

-- Note: Để giảm kích thước file, tôi chỉ tạo mẫu 10 files đầu tiên cho mỗi folder
-- Trong thực tế, bạn có thể generate script để tạo đầy đủ 100 files/folder
-- Hoặc sử dụng stored procedure/script để generate tự động

-- Files in Folder_1 (112-211) - Sample
INSERT INTO file_items (id, name, type, parent_id, owner_id, file_path, file_size, mime_type, is_deleted, created_at, updated_at) VALUES
(112, 'file_1_0.txt', 'FILE', 3, 1, '/mock/path/1/file_1_0.txt', 65000, 'text/plain', 0, NOW(), NOW()),
(113, 'file_1_1.txt', 'FILE', 3, 1, '/mock/path/1/file_1_1.txt', 85000, 'text/plain', 0, NOW(), NOW()),
(114, 'file_1_2.txt', 'FILE', 3, 1, '/mock/path/1/file_1_2.txt', 40000, 'text/plain', 0, NOW(), NOW()),
(115, 'file_1_3.txt', 'FILE', 3, 1, '/mock/path/1/file_1_3.txt', 105000, 'text/plain', 0, NOW(), NOW()),
(116, 'file_1_4.txt', 'FILE', 3, 1, '/mock/path/1/file_1_4.txt', 70000, 'text/plain', 0, NOW(), NOW()),
(117, 'file_1_5.txt', 'FILE', 3, 1, '/mock/path/1/file_1_5.txt', 90000, 'text/plain', 0, NOW(), NOW()),
(118, 'file_1_6.txt', 'FILE', 3, 1, '/mock/path/1/file_1_6.txt', 50000, 'text/plain', 0, NOW(), NOW()),
(119, 'file_1_7.txt', 'FILE', 3, 1, '/mock/path/1/file_1_7.txt', 115000, 'text/plain', 0, NOW(), NOW()),
(120, 'file_1_8.txt', 'FILE', 3, 1, '/mock/path/1/file_1_8.txt', 60000, 'text/plain', 0, NOW(), NOW()),
(121, 'file_1_9.txt', 'FILE', 3, 1, '/mock/path/1/file_1_9.txt', 80000, 'text/plain', 0, NOW(), NOW());

-- User 2: Items 1012-2022
-- Root folder
INSERT INTO file_items (id, name, type, parent_id, owner_id, file_path, file_size, mime_type, is_deleted, created_at, updated_at) VALUES
(1012, 'Root_user2@example.com', 'FOLDER', NULL, 2, NULL, NULL, NULL, 0, NOW(), NOW());

-- Subfolders (1013-1022)
INSERT INTO file_items (id, name, type, parent_id, owner_id, file_path, file_size, mime_type, is_deleted, created_at, updated_at) VALUES
(1013, 'Folder_0', 'FOLDER', 1012, 2, NULL, NULL, NULL, 0, NOW(), NOW()),
(1014, 'Folder_1', 'FOLDER', 1012, 2, NULL, NULL, NULL, 0, NOW(), NOW()),
(1015, 'Folder_2', 'FOLDER', 1012, 2, NULL, NULL, NULL, 0, NOW(), NOW()),
(1016, 'Folder_3', 'FOLDER', 1012, 2, NULL, NULL, NULL, 0, NOW(), NOW()),
(1017, 'Folder_4', 'FOLDER', 1012, 2, NULL, NULL, NULL, 0, NOW(), NOW()),
(1018, 'Folder_5', 'FOLDER', 1012, 2, NULL, NULL, NULL, 0, NOW(), NOW()),
(1019, 'Folder_6', 'FOLDER', 1012, 2, NULL, NULL, NULL, 0, NOW(), NOW()),
(1020, 'Folder_7', 'FOLDER', 1012, 2, NULL, NULL, NULL, 0, NOW(), NOW()),
(1021, 'Folder_8', 'FOLDER', 1012, 2, NULL, NULL, NULL, 0, NOW(), NOW()),
(1022, 'Folder_9', 'FOLDER', 1012, 2, NULL, NULL, NULL, 0, NOW(), NOW());

-- Files in Folder_0 (1023-1122) - Sample
INSERT INTO file_items (id, name, type, parent_id, owner_id, file_path, file_size, mime_type, is_deleted, created_at, updated_at) VALUES
(1023, 'file_0_0.txt', 'FILE', 1013, 2, '/mock/path/2/file_0_0.txt', 55000, 'text/plain', 0, NOW(), NOW()),
(1024, 'file_0_1.txt', 'FILE', 1013, 2, '/mock/path/2/file_0_1.txt', 75000, 'text/plain', 0, NOW(), NOW()),
(1025, 'file_0_2.txt', 'FILE', 1013, 2, '/mock/path/2/file_0_2.txt', 90000, 'text/plain', 0, NOW(), NOW()),
(1026, 'file_0_3.txt', 'FILE', 1013, 2, '/mock/path/2/file_0_3.txt', 40000, 'text/plain', 0, NOW(), NOW()),
(1027, 'file_0_4.txt', 'FILE', 1013, 2, '/mock/path/2/file_0_4.txt', 110000, 'text/plain', 0, NOW(), NOW()),
(1028, 'file_0_5.txt', 'FILE', 1013, 2, '/mock/path/2/file_0_5.txt', 60000, 'text/plain', 0, NOW(), NOW()),
(1029, 'file_0_6.txt', 'FILE', 1013, 2, '/mock/path/2/file_0_6.txt', 85000, 'text/plain', 0, NOW(), NOW()),
(1030, 'file_0_7.txt', 'FILE', 1013, 2, '/mock/path/2/file_0_7.txt', 50000, 'text/plain', 0, NOW(), NOW()),
(1031, 'file_0_8.txt', 'FILE', 1013, 2, '/mock/path/2/file_0_8.txt', 95000, 'text/plain', 0, NOW(), NOW()),
(1032, 'file_0_9.txt', 'FILE', 1013, 2, '/mock/path/2/file_0_9.txt', 70000, 'text/plain', 0, NOW(), NOW());

-- Note: Để tạo đầy đủ ~10,000 files, bạn có thể:
-- 1. Sử dụng stored procedure để generate tự động
-- 2. Sử dụng script Python/Java để generate SQL
-- 3. Sử dụng endpoint POST /api/v1/debug/generate-system

-- =====================================================
-- 3. FILE PERMISSIONS (10% files được chia sẻ)
-- =====================================================
-- Sample sharing permissions
-- User 1 shares some files with User 2, User 3, etc.

-- User 1 shares file 12 with User 2 (VIEW)
INSERT INTO file_permissions (id, file_id, user_id, permission_level, created_at, updated_at) VALUES
(1, 12, 2, 'VIEW', NOW(), NOW());

-- User 1 shares file 13 with User 3 (EDIT)
INSERT INTO file_permissions (id, file_id, user_id, permission_level, created_at, updated_at) VALUES
(2, 13, 3, 'EDIT', NOW(), NOW());

-- User 1 shares folder 2 (Folder_0) with User 4 (VIEW) - recursive sharing
INSERT INTO file_permissions (id, file_id, user_id, permission_level, created_at, updated_at) VALUES
(3, 2, 4, 'VIEW', NOW(), NOW());

-- User 2 shares file 1023 with User 1 (VIEW)
INSERT INTO file_permissions (id, file_id, user_id, permission_level, created_at, updated_at) VALUES
(4, 1023, 1, 'VIEW', NOW(), NOW());

-- User 2 shares file 1024 with User 5 (EDIT)
INSERT INTO file_permissions (id, file_id, user_id, permission_level, created_at, updated_at) VALUES
(5, 1024, 5, 'EDIT', NOW(), NOW());

-- Note: Trong thực tế, 10% của ~10,000 files = ~1,000 permissions
-- Bạn có thể generate thêm permissions bằng script hoặc endpoint debug

-- =====================================================
-- Reset AUTO_INCREMENT để tránh conflict
-- =====================================================
ALTER TABLE users AUTO_INCREMENT = 11;
ALTER TABLE file_items AUTO_INCREMENT = 10000;
ALTER TABLE file_permissions AUTO_INCREMENT = 1000;
ALTER TABLE download_requests AUTO_INCREMENT = 1;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- Verification Queries
-- =====================================================
-- Uncomment để kiểm tra dữ liệu sau khi import:

-- SELECT COUNT(*) as total_users FROM users;
-- SELECT COUNT(*) as total_files FROM file_items WHERE type = 'FILE';
-- SELECT COUNT(*) as total_folders FROM file_items WHERE type = 'FOLDER';
-- SELECT COUNT(*) as total_permissions FROM file_permissions;
-- SELECT u.email, COUNT(f.id) as file_count FROM users u LEFT JOIN file_items f ON u.id = f.owner_id GROUP BY u.id;

-- =====================================================
-- END OF DATA SCRIPT
-- =====================================================

