package com.inn.cafe.utils;

import com.inn.cafe.wrapper.MenuCategoryWrapper;
import com.inn.cafe.wrapper.MenuItemWrapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Utility class for generating menu PDFs using iText library
 */
@Slf4j
public class MenuPdfGenerator {

    private MenuPdfGenerator() {
    }

    /**
     * Generate menu PDF and return as byte array
     *
     * @param categories  List of menu categories with items
     * @param showVegOnly Filter to show only vegetarian items
     * @return PDF as byte array
     * @throws Exception if PDF generation fails
     */
    public static byte[] generateMenuPdf(List<MenuCategoryWrapper> categories, boolean showVegOnly) throws Exception {
        log.info("Generating menu PDF, showVegOnly: {}", showVegOnly);

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Add restaurant header
        addRestaurantHeader(document);

        // Add legend
        addLegend(document);

        // Add separator
        addSeparator(document);

        // Add categories and items
        for (MenuCategoryWrapper category : categories) {
            List<MenuItemWrapper> items = category.getItems();

            // Filter for veg only if required
            if (showVegOnly) {
                items = items.stream()
                        .filter(item -> item.getIsVeg() != null && item.getIsVeg())
                        .collect(java.util.stream.Collectors.toList());
            }

            // Skip empty categories
            if (items.isEmpty()) {
                continue;
            }

            // Add category title
            addCategoryTitle(document, category.getCategoryName());

            // Add items table
            addItemsTable(document, items);

            // Add spacing between categories
            document.add(Chunk.NEWLINE);
        }

        // Add footer
        addFooter(document);

        document.close();

        byte[] pdfBytes = baos.toByteArray();
        baos.close();

        log.info("Menu PDF generated successfully, size: {} bytes", pdfBytes.length);
        return pdfBytes;
    }

    private static void addRestaurantHeader(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 14, BaseColor.DARK_GRAY);

        Paragraph title = new Paragraph("Café Menu", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        document.add(title);

        Paragraph subtitle = new Paragraph("Mid-Range Indian Café & Restaurant", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(15);
        document.add(subtitle);
    }

    private static void addLegend(Document document) throws DocumentException {
        Font legendFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);

        PdfPTable legendTable = new PdfPTable(2);
        legendTable.setWidthPercentage(50);
        legendTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        legendTable.setSpacingAfter(10);

        // Veg symbol
        PdfPCell vegCell = new PdfPCell();
        vegCell.setBorder(Rectangle.NO_BORDER);
        vegCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Phrase vegPhrase = new Phrase();
        vegPhrase.add(new Chunk("■ ", FontFactory.getFont(FontFactory.HELVETICA, 12, new BaseColor(16, 185, 129))));
        vegPhrase.add(new Chunk("Vegetarian", legendFont));
        vegCell.setPhrase(vegPhrase);
        legendTable.addCell(vegCell);

        // Non-veg symbol
        PdfPCell nonVegCell = new PdfPCell();
        nonVegCell.setBorder(Rectangle.NO_BORDER);
        nonVegCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        Phrase nonVegPhrase = new Phrase();
        nonVegPhrase.add(new Chunk("■ ", FontFactory.getFont(FontFactory.HELVETICA, 12, new BaseColor(239, 68, 68))));
        nonVegPhrase.add(new Chunk("Non-Vegetarian", legendFont));
        nonVegCell.setPhrase(nonVegPhrase);
        legendTable.addCell(nonVegCell);

        document.add(legendTable);
    }

    private static void addSeparator(Document document) throws DocumentException {
        Paragraph separator = new Paragraph();
        separator.add(new Chunk(new com.itextpdf.text.pdf.draw.LineSeparator()));
        separator.setSpacingAfter(10);
        document.add(separator);
    }

    private static void addCategoryTitle(Document document, String categoryName) throws DocumentException {
        Font categoryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(102, 126, 234));
        Paragraph categoryTitle = new Paragraph(categoryName, categoryFont);
        categoryTitle.setSpacingBefore(10);
        categoryTitle.setSpacingAfter(8);
        document.add(categoryTitle);
    }

    private static void addItemsTable(Document document, List<MenuItemWrapper> items) throws DocumentException {
        PdfPTable table = new PdfPTable(3); // Symbol, Name/Description, Price
        table.setWidthPercentage(100);
        table.setSpacingAfter(5);

        try {
            table.setWidths(new float[] { 1f, 10f, 2f });
        } catch (DocumentException e) {
            log.error("Error setting column widths", e);
        }

        Font itemNameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font itemDescFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
        Font priceFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);

        for (MenuItemWrapper item : items) {
            // Veg/Non-veg indicator
            PdfPCell symbolCell = new PdfPCell();
            symbolCell.setBorder(Rectangle.NO_BORDER);
            symbolCell.setPadding(5);
            symbolCell.setVerticalAlignment(Element.ALIGN_TOP);

            BaseColor symbolColor = (item.getIsVeg() != null && item.getIsVeg())
                    ? new BaseColor(16, 185, 129) // Green for veg
                    : new BaseColor(239, 68, 68); // Red for non-veg

            symbolCell.setPhrase(new Phrase("■", FontFactory.getFont(FontFactory.HELVETICA, 12, symbolColor)));
            table.addCell(symbolCell);

            // Name and description
            PdfPCell nameDescCell = new PdfPCell();
            nameDescCell.setBorder(Rectangle.NO_BORDER);
            nameDescCell.setPadding(5);

            Paragraph nameDescPara = new Paragraph();
            nameDescPara.add(new Chunk(item.getProductName(), itemNameFont));
            nameDescPara.add(Chunk.NEWLINE);
            nameDescPara.add(new Chunk(item.getDescription(), itemDescFont));
            nameDescCell.addElement(nameDescPara);
            table.addCell(nameDescCell);

            // Price
            PdfPCell priceCell = new PdfPCell(new Phrase("₹" + item.getPrice(), priceFont));
            priceCell.setBorder(Rectangle.NO_BORDER);
            priceCell.setPadding(5);
            priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            priceCell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(priceCell);

            // Add separator row
            PdfPCell separatorCell = new PdfPCell();
            separatorCell.setColspan(3);
            separatorCell.setBorder(Rectangle.BOTTOM);
            separatorCell.setBorderColor(BaseColor.LIGHT_GRAY);
            separatorCell.setPadding(2);
            table.addCell(separatorCell);
        }

        document.add(table);
    }

    private static void addFooter(Document document) throws DocumentException {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.GRAY);

        document.add(Chunk.NEWLINE);

        Paragraph thankYou = new Paragraph("Thank you for dining with us!", footerFont);
        thankYou.setAlignment(Element.ALIGN_CENTER);
        thankYou.setSpacingAfter(5);
        document.add(thankYou);

        Paragraph taxes = new Paragraph("Prices are inclusive of applicable taxes", smallFont);
        taxes.setAlignment(Element.ALIGN_CENTER);
        document.add(taxes);
    }
}
