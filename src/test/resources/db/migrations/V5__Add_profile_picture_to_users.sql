-- Add profile_picture column to users table
-- Check if column exists first using a stored procedure approach
SET @dbname = DATABASE();
SET @tablename = 'users';
SET @columnname = 'profile_picture';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      TABLE_SCHEMA = @dbname
      AND TABLE_NAME = @tablename
      AND COLUMN_NAME = @columnname
  ) > 0,
  'SELECT 1',
  'ALTER TABLE users ADD COLUMN profile_picture VARCHAR(255) COMMENT ''Path to user profile picture file'''
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;
