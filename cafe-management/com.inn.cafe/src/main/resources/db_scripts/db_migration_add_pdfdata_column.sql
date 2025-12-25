-- Migration script to add pdfdata column to bill table
-- This script adds a LONGBLOB column to store PDF data in the database

-- Add pdfdata column to bill table
ALTER TABLE bill ADD COLUMN pdfdata LONGBLOB;

-- Optional: Add comment to the column for documentation
ALTER TABLE bill MODIFY COLUMN pdfdata LONGBLOB COMMENT 'Stores PDF bill as binary data';
