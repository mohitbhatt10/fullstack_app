package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Bill;
import com.inn.cafe.POJO.DraftOrder;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.BillDao;
import com.inn.cafe.dao.DraftOrderDao;
import com.inn.cafe.service.BillService;
import com.inn.cafe.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    BillDao billDao;

    @Autowired
    DraftOrderDao draftOrderDao;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generateReport");
        try {
            String fileName;
            if (validateRequestMap(requestMap)) {
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
                    fileName = (String) requestMap.get("uuid");
                } else {
                    fileName = CafeUtils.getUUID();
                    requestMap.put("uuid", fileName);
                }

                // Generate PDF in memory using ByteArrayOutputStream
                Document document = new Document();
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                PdfWriter.getInstance(document, baos);

                document.open();

                // Add restaurant header with professional styling
                addRestaurantHeader(document);

                // Add bill number and date section
                addBillDetails(document, fileName);

                // Add customer information section
                addCustomerInfo(document, requestMap);

                // Add separator line
                addSeparator(document);

                // Add itemized product table
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);
                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJSonArrayFromString((String) requestMap.get("productDetails"));
                double subtotal = 0.0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    Map<String, Object> productData = CafeUtils.getMapFromJson(jsonArray.getString(i));
                    addRows(table, productData);
                    subtotal += (Double) productData.get("total");
                }
                document.add(table);

                // Add pricing breakdown (subtotal, tax, total)
                addPricingSummary(document, requestMap, subtotal);

                // Add payment information
                addPaymentInfo(document, requestMap);

                // Add footer with thank you message
                addFooter(document);

                document.close();

                // Get PDF bytes from ByteArrayOutputStream
                byte[] pdfBytes = baos.toByteArray();
                baos.close();

                // Save bill with PDF data to database
                requestMap.put("pdfData", pdfBytes);
                insertBill(requestMap);

                // If this bill was generated from a draft, remove the draft after successful
                // PDF generation.
                if (requestMap.containsKey("draftId") && requestMap.get("draftId") != null) {
                    try {
                        Integer draftId = Integer.parseInt(String.valueOf(requestMap.get("draftId")));
                        draftOrderDao.deleteById(draftId);
                    } catch (Exception ignored) {
                        // ignore cleanup failures, bill generation already succeeded
                    }
                }
                return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);

            }
            return CafeUtils.getResponseEntity("Required data not found", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addRows(PdfPTable table, Map<String, Object> data) {
        log.info("Inside addRows");
        PdfPCell cell;

        // Name cell (left aligned)
        cell = new PdfPCell(new Phrase((String) data.get("name"), getFont("Data")));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        // Category cell (left aligned)
        cell = new PdfPCell(new Phrase((String) data.get("category"), getFont("Data")));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        // Quantity cell (center aligned)
        cell = new PdfPCell(new Phrase((String) data.get("quantity"), getFont("Data")));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        // Price cell (right aligned)
        cell = new PdfPCell(new Phrase("₹ " + String.format("%.2f", (Double) data.get("price")), getFont("Data")));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        // Total cell (right aligned)
        cell = new PdfPCell(new Phrase("₹ " + String.format("%.2f", (Double) data.get("total")), getFont("Data")));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader");
        Stream.of("Item Name", "Category", "Qty", "Unit Price", "Amount")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(new BaseColor(70, 130, 180)); // Steel Blue
                    header.setBorderWidth(1);
                    header.setBorderColor(BaseColor.WHITE);
                    header.setPhrase(new Phrase(columnTitle,
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    header.setPadding(8);
                    table.addCell(header);
                });
    }

    private Font getFont(String type) {
        log.info("Inside getFont");
        switch (type) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "SubHeader":
                Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(70, 130, 180));
                return subHeaderFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            case "Small":
                Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
                return smallFont;
            case "Bold":
                Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
                return boldFont;
            default:
                return new Font();
        }
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());

            // Set PDF data if available
            if (requestMap.containsKey("pdfData")) {
                bill.setPdfData((byte[]) requestMap.get("pdfData"));
            }

            billDao.save(bill);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyDraftFields(DraftOrder draftOrder, Map<String, Object> requestMap) {
        draftOrder.setName((String) requestMap.get("name"));
        draftOrder.setEmail((String) requestMap.get("email"));
        draftOrder.setContactNumber((String) requestMap.get("contactNumber"));
        draftOrder.setPaymentMethod((String) requestMap.get("paymentMethod"));
        draftOrder.setTotal(Integer.parseInt(String.valueOf(requestMap.get("totalAmount"))));
        draftOrder.setProductDetails((String) requestMap.get("productDetails"));
        draftOrder.setUpdatedBy(jwtFilter.getCurrentUser());
        if (draftOrder.getCreatedBy() == null) {
            draftOrder.setCreatedBy(jwtFilter.getCurrentUser());
        }
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        List<Bill> list = new ArrayList<>();
        if (jwtFilter.isAdmin()) {
            list = billDao.getAllBills();
        } else {
            list = billDao.getBillByUserName(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Inside getPdf : requestMap {}", requestMap);
        try {
            byte[] byteArray = new byte[0];
            if (!requestMap.containsKey("uuid"))
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);

            String uuid = (String) requestMap.get("uuid");

            // Try to get bill from database by UUID
            Optional<Bill> billOptional = billDao.findByUuid(uuid);

            if (billOptional.isPresent()) {
                Bill bill = billOptional.get();

                // Check if PDF data exists in database
                if (bill.getPdfData() != null && bill.getPdfData().length > 0) {
                    byteArray = bill.getPdfData();
                    return new ResponseEntity<>(byteArray, HttpStatus.OK);
                } else {
                    // If PDF data doesn't exist, regenerate it
                    requestMap.put("isGenerate", false);
                    requestMap.put("name", bill.getName());
                    requestMap.put("email", bill.getEmail());
                    requestMap.put("contactNumber", bill.getContactNumber());
                    requestMap.put("paymentMethod", bill.getPaymentMethod());
                    requestMap.put("totalAmount", String.valueOf(bill.getTotal()));
                    requestMap.put("productDetails", bill.getProductDetails());

                    generateReport(requestMap);

                    // Retrieve the updated bill with PDF data
                    billOptional = billDao.findByUuid(uuid);
                    if (billOptional.isPresent() && billOptional.get().getPdfData() != null) {
                        byteArray = billOptional.get().getPdfData();
                        return new ResponseEntity<>(byteArray, HttpStatus.OK);
                    }
                }
            } else {
                return new ResponseEntity<>(byteArray, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            Optional optional = billDao.findById(id);
            if (!optional.isEmpty()) {
                billDao.deleteById(id);
                return CafeUtils.getResponseEntity("Bill deleted Successfully.", HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity("Bill id does not exist.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> saveDraft(Map<String, Object> requestMap) {
        try {
            if (!validateRequestMap(requestMap)) {
                return CafeUtils.getResponseEntity("Required data not found", HttpStatus.BAD_REQUEST);
            }

            DraftOrder draftOrder;
            if (requestMap.containsKey("id") && requestMap.get("id") != null
                    && !String.valueOf(requestMap.get("id")).isBlank()) {
                Integer id = Integer.parseInt(String.valueOf(requestMap.get("id")));
                Optional<DraftOrder> optionalDraft = draftOrderDao.findById(id);
                if (optionalDraft.isEmpty()) {
                    return CafeUtils.getResponseEntity("Draft order id does not exist.", HttpStatus.OK);
                }
                draftOrder = optionalDraft.get();

                String currentUser = jwtFilter.getCurrentUser();
                String createdBy = draftOrder.getCreatedBy();
                if (!jwtFilter.isAdmin() && createdBy != null && !createdBy.equalsIgnoreCase(currentUser)) {
                    return CafeUtils.getResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED);
                }
                if (!jwtFilter.isAdmin() && createdBy == null) {
                    draftOrder.setCreatedBy(currentUser);
                }
            } else {
                draftOrder = new DraftOrder();
                draftOrder.setCreatedBy(jwtFilter.getCurrentUser());
            }

            applyDraftFields(draftOrder, requestMap);
            DraftOrder saved = draftOrderDao.save(draftOrder);
            return new ResponseEntity<>("{\"id\":" + saved.getId() + "}", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<DraftOrder>> getDrafts() {
        try {
            List<DraftOrder> list;
            if (jwtFilter.isAdmin()) {
                list = draftOrderDao.getAllDraftOrders();
            } else {
                list = draftOrderDao.findByCreatedByOrderByUpdatedAtDesc(jwtFilter.getCurrentUser());
            }
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<DraftOrder> getDraftById(Integer id) {
        try {
            Optional<DraftOrder> optionalDraft = draftOrderDao.findById(id);
            if (optionalDraft.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            }

            DraftOrder draftOrder = optionalDraft.get();
            String createdBy = draftOrder.getCreatedBy();
            if (!jwtFilter.isAdmin() && createdBy != null && !createdBy.equalsIgnoreCase(jwtFilter.getCurrentUser())) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }

            if (!jwtFilter.isAdmin() && createdBy == null) {
                draftOrder.setCreatedBy(jwtFilter.getCurrentUser());
                draftOrderDao.save(draftOrder);
            }

            return new ResponseEntity<>(draftOrder, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteDraft(Integer id) {
        try {
            Optional<DraftOrder> optionalDraft = draftOrderDao.findById(id);
            if (optionalDraft.isPresent()) {
                DraftOrder draftOrder = optionalDraft.get();
                String createdBy = draftOrder.getCreatedBy();
                if (!jwtFilter.isAdmin() && createdBy != null
                        && !createdBy.equalsIgnoreCase(jwtFilter.getCurrentUser())) {
                    return CafeUtils.getResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED);
                }

                draftOrderDao.deleteById(id);
                return CafeUtils.getResponseEntity("Draft order deleted Successfully.", HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity("Draft order id does not exist.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ========== New Helper Methods for Enhanced Bill Template ==========

    private void addRestaurantHeader(Document document) throws DocumentException {
        // Restaurant name with large, bold font
        Paragraph restaurantName = new Paragraph("Cafe Management System",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new BaseColor(70, 130, 180)));
        restaurantName.setAlignment(Element.ALIGN_CENTER);
        restaurantName.setSpacingAfter(5f);
        document.add(restaurantName);

        // Restaurant address and contact details
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);
        Paragraph address = new Paragraph("123 Main Street, City Center, State - 123456", smallFont);
        address.setAlignment(Element.ALIGN_CENTER);
        document.add(address);

        Paragraph contact = new Paragraph("Phone: +91-1234567890 | Email: info@cafemanagement.com", smallFont);
        contact.setAlignment(Element.ALIGN_CENTER);
        contact.setSpacingAfter(5f);
        document.add(contact);

        Paragraph gst = new Paragraph("GSTIN: 22AAAAA0000A1Z5 | FSSAI Lic No: 12345678901234", smallFont);
        gst.setAlignment(Element.ALIGN_CENTER);
        gst.setSpacingAfter(10f);
        document.add(gst);
    }

    private void addBillDetails(Document document, String billNumber) throws DocumentException {
        PdfPTable billInfoTable = new PdfPTable(2);
        billInfoTable.setWidthPercentage(100);
        billInfoTable.setSpacingAfter(5f);

        // Bill number (left)
        PdfPCell billNoCell = new PdfPCell(new Phrase("Bill No: " + billNumber, getFont("Bold")));
        billNoCell.setBorder(Rectangle.NO_BORDER);
        billNoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        billInfoTable.addCell(billNoCell);

        // Date (right)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String currentDate = sdf.format(new java.util.Date());
        PdfPCell dateCell = new PdfPCell(new Phrase("Date: " + currentDate, getFont("Bold")));
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        billInfoTable.addCell(dateCell);

        document.add(billInfoTable);
    }

    private void addCustomerInfo(Document document, Map<String, Object> requestMap) throws DocumentException {
        Paragraph customerHeader = new Paragraph("CUSTOMER DETAILS", getFont("SubHeader"));
        customerHeader.setSpacingBefore(10f);
        customerHeader.setSpacingAfter(5f);
        document.add(customerHeader);

        Font dataFont = getFont("Data");

        PdfPTable customerTable = new PdfPTable(2);
        customerTable.setWidthPercentage(100);
        customerTable.setWidths(new float[] { 1, 2 });

        // Customer Name
        addCustomerInfoRow(customerTable, "Customer Name:", (String) requestMap.get("name"));

        // Contact Number
        addCustomerInfoRow(customerTable, "Contact Number:", (String) requestMap.get("contactNumber"));

        // Email
        addCustomerInfoRow(customerTable, "Email:", (String) requestMap.get("email"));

        document.add(customerTable);
    }

    private void addCustomerInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, getFont("Bold")));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(3);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, getFont("Data")));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(3);
        table.addCell(valueCell);
    }

    private void addSeparator(Document document) throws DocumentException {
        Paragraph separator = new Paragraph("―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
        separator.setAlignment(Element.ALIGN_CENTER);
        separator.setSpacingBefore(10f);
        separator.setSpacingAfter(5f);
        document.add(separator);
    }

    private void addPricingSummary(Document document, Map<String, Object> requestMap, double subtotal)
            throws DocumentException {
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new float[] { 3, 1 });
        summaryTable.setSpacingBefore(10f);

        // Subtotal
        addSummaryRow(summaryTable, "Subtotal:", String.format("₹ %.2f", subtotal), false);

        // Calculate tax (5% GST as default)
        double taxRate = 0.05;
        double taxAmount = subtotal * taxRate;
        addSummaryRow(summaryTable, "GST (5%):", String.format("₹ %.2f", taxAmount), false);

        // Discount (if any) - default to 0
        double discount = 0.0;
        if (requestMap.containsKey("discount")) {
            try {
                discount = Double.parseDouble(String.valueOf(requestMap.get("discount")));
                addSummaryRow(summaryTable, "Discount:", String.format("- ₹ %.2f", discount), false);
            } catch (Exception ignored) {
            }
        }

        // Add separator line
        PdfPCell separatorCell = new PdfPCell(new Phrase(""));
        separatorCell.setColspan(2);
        separatorCell.setBorder(Rectangle.TOP);
        separatorCell.setBorderWidth(1);
        separatorCell.setPadding(5);
        summaryTable.addCell(separatorCell);

        // Total amount (bold and larger)
        double totalAmount = Double.parseDouble((String) requestMap.get("totalAmount"));
        addSummaryRow(summaryTable, "TOTAL AMOUNT:", String.format("₹ %.2f", totalAmount), true);

        document.add(summaryTable);
    }

    private void addSummaryRow(PdfPTable table, String label, String value, boolean isBold) {
        Font font = isBold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.BLACK) : getFont("Data");

        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addPaymentInfo(Document document, Map<String, Object> requestMap) throws DocumentException {
        Paragraph paymentHeader = new Paragraph("PAYMENT INFORMATION", getFont("SubHeader"));
        paymentHeader.setSpacingBefore(15f);
        paymentHeader.setSpacingAfter(5f);
        document.add(paymentHeader);

        String paymentMethod = (String) requestMap.get("paymentMethod");
        Paragraph paymentDetails = new Paragraph("Payment Method: " + paymentMethod, getFont("Bold"));
        paymentDetails.setSpacingAfter(5f);
        document.add(paymentDetails);

        Paragraph paymentStatus = new Paragraph("Payment Status: PAID", getFont("Data"));
        document.add(paymentStatus);
    }

    private void addFooter(Document document) throws DocumentException {
        addSeparator(document);

        Paragraph thankYou = new Paragraph("Thank You for Your Visit!",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new BaseColor(70, 130, 180)));
        thankYou.setAlignment(Element.ALIGN_CENTER);
        thankYou.setSpacingBefore(15f);
        thankYou.setSpacingAfter(5f);
        document.add(thankYou);

        Paragraph visitAgain = new Paragraph("We hope to serve you again soon!", getFont("Data"));
        visitAgain.setAlignment(Element.ALIGN_CENTER);
        document.add(visitAgain);

        Paragraph termsNote = new Paragraph(
                "\nNote: This is a computer-generated bill and does not require a signature.",
                getFont("Small"));
        termsNote.setAlignment(Element.ALIGN_CENTER);
        termsNote.setSpacingBefore(10f);
        document.add(termsNote);
    }

}
