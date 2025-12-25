# Quick Setup Guide - Database Bill Storage

## Step-by-Step Setup

### 1. Apply Database Migration

Choose one of these methods:

**Option A: Using MySQL Command Line**

```bash
mysql -u your_username -p your_database_name < src/main/resources/db_migration_add_pdfdata_column.sql
```

**Option B: Using MySQL Workbench**

1. Open MySQL Workbench
2. Connect to your database
3. Open the file: `src/main/resources/db_migration_add_pdfdata_column.sql`
4. Execute the script

**Option C: Direct SQL Execution**

```sql
USE your_database_name;
ALTER TABLE bill ADD COLUMN pdfdata LONGBLOB;
```

### 2. Verify Database Changes

```sql
DESCRIBE bill;
```

You should see the new `pdfdata` column with type `longblob`.

### 3. Build the Backend

```bash
cd cafe-management/com.inn.cafe
mvn clean package -DskipTests
```

### 4. Restart Application

Stop and restart your Spring Boot application to load the new code.

### 5. Test the Functionality

**Test 1: Generate a New Bill**

1. Go to "Manage Order" or bill generation page
2. Create a new bill
3. Verify it's saved successfully

**Test 2: Download Bill**

1. Go to "View Bills" page
2. Click the download icon on any bill
3. Verify PDF downloads correctly

**Test 3: View Bill Details**

1. Click the "View" icon on any bill
2. Verify bill details display correctly

## Verification Queries

Check if PDFs are being stored in database:

```sql
-- Check total bills with PDF data
SELECT COUNT(*) as bills_with_pdf
FROM bill
WHERE pdfdata IS NOT NULL;

-- Check PDF sizes
SELECT id, uuid, name, LENGTH(pdfdata) as pdf_size_bytes
FROM bill
WHERE pdfdata IS NOT NULL
LIMIT 10;

-- Check for bills without PDF data (old bills)
SELECT COUNT(*) as bills_without_pdf
FROM bill
WHERE pdfdata IS NULL;
```

## Troubleshooting

### Problem: "Unknown column 'pdfdata'"

**Solution**: Database migration not applied. Run Step 1 again.

### Problem: Bills download but PDF is empty

**Solution**:

1. Check application logs for errors
2. Verify database connection is working
3. Check if PDF data is actually stored:
   ```sql
   SELECT uuid, LENGTH(pdfdata) FROM bill WHERE uuid = 'your-bill-uuid';
   ```

### Problem: "OutOfMemoryError" when generating bills

**Solution**: Increase JVM heap size:

```bash
java -Xmx1024m -jar your-application.jar
```

### Problem: Old bills (created before migration) show errors

**Solution**: This is normal. On first download, the system will:

1. Detect missing PDF data
2. Regenerate the PDF from bill data
3. Save it to database
4. Subsequent downloads will work normally

## Database Cleanup (Optional)

If you have old PDF files in the filesystem that you want to clean up:

1. **Backup first!**

   ```bash
   cd cafe-management/com.inn.cafe/src/main/resources
   mkdir pdf_backup
   mv *.pdf pdf_backup/
   ```

2. After verifying all bills can be downloaded from database, you can delete old files:
   ```bash
   rm -rf pdf_backup/
   ```

## Performance Monitoring

Monitor database size growth:

```sql
SELECT
    table_name AS 'Table',
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)'
FROM information_schema.TABLES
WHERE table_schema = 'your_database_name'
    AND table_name = 'bill';
```

## Success Indicators

✅ Database migration completed without errors
✅ Application starts successfully
✅ New bills are generated and saved
✅ Bills can be downloaded from "View Bills" page
✅ Database queries show pdfdata is being populated
✅ No errors in application logs

## Next Steps

Once everything is working:

1. Test with multiple users
2. Monitor database performance
3. Set up regular database backups
4. Consider database optimization if needed

## Support

If you encounter issues:

1. Check application logs: `logs/cafe-management.log`
2. Check database connection
3. Verify migration was applied correctly
4. Review error messages carefully

## Rollback (If Needed)

If something goes wrong and you need to rollback:

1. Stop the application
2. Deploy the previous version of the code
3. The pdfdata column can remain (it won't cause issues)
4. Old filesystem-based code will continue working
