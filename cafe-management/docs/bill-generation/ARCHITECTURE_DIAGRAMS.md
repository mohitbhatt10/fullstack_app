# Bill Storage Architecture

## Before: Filesystem Storage

```
┌─────────────────────────────────────────────────────────────┐
│                        User Actions                          │
└──────────────────┬──────────────────────────────────────────┘
                   │
         ┌─────────▼─────────┐
         │  Generate Bill    │
         └─────────┬─────────┘
                   │
         ┌─────────▼─────────────────────────────────┐
         │  BillServiceImpl.generateReport()         │
         │  - Creates Document                        │
         │  - Uses FileOutputStream                   │
         │  - Saves to: src/main/resources/*.pdf     │
         └─────────┬─────────────────────────────────┘
                   │
         ┌─────────▼─────────────┐      ┌─────────────────┐
         │  Database (bill table)│      │   Filesystem    │
         │  - id                 │      │  Bill-xxx.pdf   │
         │  - uuid               │      │  Bill-yyy.pdf   │
         │  - name               │      │  Bill-zzz.pdf   │
         │  - email              │      └─────────────────┘
         │  - contactNumber      │               │
         │  - paymentMethod      │               │
         │  - total              │               │
         │  - productDetails     │               │
         │  - createdBy          │               │
         └───────────────────────┘               │
                                                  │
         ┌─────────────────────┐                 │
         │   Download Bill     │                 │
         └─────────┬───────────┘                 │
                   │                             │
         ┌─────────▼──────────────────────┐     │
         │  BillServiceImpl.getPdf()      │     │
         │  - Reads from filesystem       │◄────┘
         │  - Uses FileInputStream        │
         └─────────┬──────────────────────┘
                   │
         ┌─────────▼─────────┐
         │  Return PDF Bytes │
         └───────────────────┘

❌ Issues:
- PDF files scattered in filesystem
- Requires file backup separately from database
- Problems in containerized environments
- Orphaned files possible
- No transactional consistency
```

## After: Database Storage (Current Implementation)

```
┌─────────────────────────────────────────────────────────────┐
│                        User Actions                          │
└──────────────────┬──────────────────────────────────────────┘
                   │
         ┌─────────▼─────────┐
         │  Generate Bill    │
         └─────────┬─────────┘
                   │
         ┌─────────▼─────────────────────────────────┐
         │  BillServiceImpl.generateReport()         │
         │  - Creates Document                        │
         │  - Uses ByteArrayOutputStream (memory)    │
         │  - Converts to byte[]                     │
         └─────────┬─────────────────────────────────┘
                   │
         ┌─────────▼──────────────────────────────┐
         │  insertBill()                          │
         │  - Saves all bill data to database     │
         │  - Includes PDF as BLOB (pdfData)      │
         └─────────┬──────────────────────────────┘
                   │
         ┌─────────▼─────────────────────────────┐
         │  Database (bill table)                │
         │  - id                                 │
         │  - uuid                               │
         │  - name                               │
         │  - email                              │
         │  - contactNumber                      │
         │  - paymentMethod                      │
         │  - total                              │
         │  - productDetails                     │
         │  - createdBy                          │
         │  - pdfData (LONGBLOB) ◄── PDF stored  │
         └───────────────────────────────────────┘
                   │
         ┌─────────▼───────────┐
         │   Download Bill     │
         └─────────┬───────────┘
                   │
         ┌─────────▼──────────────────────┐
         │  BillServiceImpl.getPdf()      │
         │  - Query by UUID               │
         │  - Get pdfData from database   │
         │  - Return PDF bytes directly   │
         └─────────┬──────────────────────┘
                   │
         ┌─────────▼─────────┐
         │  Return PDF Bytes │
         └───────────────────┘

✅ Benefits:
- All data in one place (database)
- Transactional consistency
- Easy backup/restore
- Cloud and container friendly
- No orphaned files
- Scalable architecture
```

## Data Flow Comparison

### OLD: Generate & Store Bill

```
Request → Service → Create PDF → Save to File → Save metadata to DB → Response
                    (iText PDF)   (Filesystem)   (bill table)
```

### NEW: Generate & Store Bill

```
Request → Service → Create PDF → Save PDF bytes to DB → Response
                    (iText PDF)   (bill.pdfData BLOB)
                                 ↓
                          All in one transaction
```

### OLD: Download Bill

```
Request → Service → Read UUID from DB → Find file in filesystem → Read file → Response
                                        (may fail if file missing)
```

### NEW: Download Bill

```
Request → Service → Query DB by UUID → Get pdfData column → Response
                    ↓
              Single query, always consistent
```

## Frontend Integration (Unchanged)

```typescript
// view-bill.component.ts
downloadReportAction(values:any){
  var data = {
    uuid: values.uuid,  // ← Only UUID needed
    name: values.name,
    email: values.email,
    // ... other metadata
  }
  this.billService.getPdf(data).subscribe((response:Blob)=>{
    saveAs(response, uuid + '.pdf');  // ← Blob saved as file
  });
}
```

Frontend doesn't need to change because:

- Still sends UUID in request
- Still receives Blob response
- Backend change is transparent

## Migration Path for Existing Bills

```
┌────────────────────┐
│  Old Bill Record   │
│  pdfData = NULL    │
└──────────┬─────────┘
           │
           │ User clicks Download
           ▼
┌────────────────────────────┐
│  getPdf() detects NULL     │
│  Regenerates PDF from data │
└──────────┬─────────────────┘
           │
           ▼
┌────────────────────────────┐
│  Update record with PDF    │
│  pdfData = [PDF bytes]     │
└──────────┬─────────────────┘
           │
           ▼
┌────────────────────────────┐
│  Future downloads use DB   │
│  No regeneration needed    │
└────────────────────────────┘
```

## Database Schema

```sql
CREATE TABLE bill (
  id INT PRIMARY KEY AUTO_INCREMENT,
  uuid VARCHAR(255),
  name VARCHAR(255),
  email VARCHAR(255),
  contactnumber VARCHAR(20),
  paymentmethod VARCHAR(50),
  total INT,
  productdetails JSON,
  createdby VARCHAR(255),
  pdfdata LONGBLOB  ← NEW: Stores PDF binary data
);

-- Optional optimization
CREATE INDEX idx_bill_uuid ON bill(uuid);
```

## Storage Capacity

```
LONGBLOB capacity: 4 GB (4,294,967,295 bytes)
Typical bill PDF size: 50-200 KB
Maximum bills per GB: ~5,000-20,000 bills
```

## Memory Considerations

```java
// OLD: Streaming to file (low memory)
FileOutputStream out = new FileOutputStream("bill.pdf");
PdfWriter.getInstance(document, out);

// NEW: In-memory generation (higher memory, but manageable)
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PdfWriter.getInstance(document, baos);
byte[] pdfBytes = baos.toByteArray();  // ~50-200 KB per bill
```

Typical bill generation uses 200-500 KB of memory temporarily.
Multiple concurrent generations are still efficient.
