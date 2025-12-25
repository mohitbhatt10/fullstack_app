# Bill Storage Migration: Filesystem to Database

## Overview

This implementation migrates bill PDF storage from the filesystem to the database as BLOB data. Bills are now stored directly in the `bill` table, eliminating the need for external file storage.

## Backend Changes

### 1. Database Schema Changes

**File**: `Bill.java`

- Added new column: `pdfData` (LONGBLOB type)
- Annotated with `@Lob` and `@Column` for large binary object storage
- Stores the complete PDF as binary data

**Migration Script**: `db_migration_add_pdfdata_column.sql`

```sql
ALTER TABLE bill ADD COLUMN pdfdata LONGBLOB;
```

### 2. DAO Layer Changes

**File**: `BillDao.java`

- Added `findByUuid(String uuid)` method
- Enables retrieval of bills by UUID for PDF download

### 3. Service Layer Changes

**File**: `BillServiceImpl.java`

#### Modified Methods:

**a) `generateReport()`**

- Changed from `FileOutputStream` to `ByteArrayOutputStream`
- PDF is generated in memory instead of filesystem
- PDF bytes are stored in the database via `insertBill()`
- No longer creates files in `src/main/resources`

**b) `insertBill()`**

- Added logic to save PDF BLOB data
- Stores `pdfData` byte array in Bill entity

**c) `getPdf()`**

- Complete rewrite to retrieve PDF from database
- Looks up bill by UUID using `billDao.findByUuid()`
- Returns PDF bytes directly from database
- Falls back to regeneration if PDF data is missing
- No longer reads from filesystem

**d) Removed Methods:**

- `getByteArray(String filePath)` - No longer needed

## Frontend Changes

### No Changes Required!

The frontend is already compatible with the new implementation:

- `BillService.getPdf()` sends UUID in request
- Backend returns PDF as blob response
- Frontend saves the blob using FileSaver

**File**: `view-bill.component.ts`

```typescript
downloadReportAction(values:any){
  var data = {
    uuid: values.uuid,  // UUID is sent to backend
    // ... other fields
  }
  this.downloadFile(values.uuid, data);
}
```

## Migration Steps

### Step 1: Apply Database Migration

Run the SQL migration script to add the `pdfdata` column:

```bash
mysql -u [username] -p [database_name] < src/main/resources/db_migration_add_pdfdata_column.sql
```

Or execute directly in your database:

```sql
ALTER TABLE bill ADD COLUMN pdfdata LONGBLOB;
```

### Step 2: Build and Deploy Backend

```bash
cd cafe-management/com.inn.cafe
mvn clean package -DskipTests
```

### Step 3: Restart Application

- Stop the Spring Boot application
- Start with the updated JAR file
- All new bills will be stored in the database

## Benefits

### 1. **Simplified Deployment**

- No external file storage management
- No need to backup/sync filesystem directories
- Database handles all data persistence

### 2. **Better Scalability**

- Works seamlessly in containerized environments
- No shared filesystem requirements
- Easy horizontal scaling

### 3. **Improved Data Integrity**

- Transactional consistency (bill record + PDF in one transaction)
- No orphaned files or missing PDFs
- Database backup includes all bill data

### 4. **Cloud-Ready**

- Compatible with cloud database services
- No need for persistent volume storage
- Works with serverless architectures

## Data Flow

### Bill Generation Flow:

```
1. User creates bill →
2. Backend generates PDF in memory (ByteArrayOutputStream) →
3. PDF bytes saved to database (pdfdata column) →
4. UUID returned to frontend
```

### Bill Download Flow:

```
1. User clicks download →
2. Frontend sends UUID to backend →
3. Backend queries database: billDao.findByUuid(uuid) →
4. PDF bytes retrieved from pdfdata column →
5. Bytes sent as response (blob) →
6. Frontend saves as PDF file
```

## Backward Compatibility

### Existing Bills

- Bills created before migration won't have PDF data
- System automatically regenerates PDF on first download
- Regenerated PDF is saved to database for future downloads

### Handling Missing PDFs:

```java
if (bill.getPdfData() == null || bill.getPdfData().length == 0) {
    // Regenerate PDF using bill data
    generateReport(requestMap);
    // PDF now stored in database
}
```

## Testing Checklist

- [ ] Database migration applied successfully
- [ ] Application starts without errors
- [ ] New bills generate and store correctly
- [ ] Bills can be downloaded from "View Bills" page
- [ ] Multiple downloads of same bill work correctly
- [ ] Old bills (without PDF data) regenerate on first download
- [ ] Bill deletion removes PDF data from database

## Performance Considerations

### Database Size

- Each PDF is approximately 50-200 KB
- LONGBLOB can store up to 4 GB
- Monitor database size as bills accumulate

### Query Performance

- PDF data is excluded from list queries (not in SELECT \*)
- Only retrieved when specifically requested (getPdf)
- Consider adding index on UUID column if needed:
  ```sql
  CREATE INDEX idx_bill_uuid ON bill(uuid);
  ```

## Troubleshooting

### Issue: "Column 'pdfdata' not found"

**Solution**: Run the database migration script

### Issue: Downloaded PDF is empty

**Solution**: Check if bill record has pdfdata. If null, it will regenerate on next download

### Issue: OutOfMemoryError

**Solution**: Increase JVM heap size or implement streaming for very large PDFs

## Future Enhancements

1. **Compression**: Compress PDF bytes before storage
2. **Caching**: Cache frequently downloaded bills
3. **Archival**: Move old bills to archive table
4. **Audit Trail**: Track PDF downloads and regenerations

## Files Changed

### Backend:

- `Bill.java` - Added pdfData field
- `BillDao.java` - Added findByUuid method
- `BillServiceImpl.java` - Updated generateReport, insertBill, getPdf
- `db_migration_add_pdfdata_column.sql` - Database migration script

### Frontend:

- No changes required (already compatible)

## Rollback Plan

If needed to rollback:

1. Revert code changes to previous version
2. Keep the `pdfdata` column (no harm, just unused)
3. System will continue using filesystem storage
