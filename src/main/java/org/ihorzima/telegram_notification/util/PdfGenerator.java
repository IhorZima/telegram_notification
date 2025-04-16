//package org.ihorzima.telegram_notification.util;
//
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.element.Cell;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.io.ByteArrayInputStream;
//
//public class PdfGenerator {
//
//    public static InputStream createPdf() throws Exception {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        PdfWriter writer = new PdfWriter(outputStream);
//        PdfDocument pdfDoc = new PdfDocument(writer);
//        Document document = new Document(pdfDoc);
//
//        // Создаём таблицу (3 колонки)
//        float[] columnWidths = {50f, 150f, 50f};
//        Table table = new Table(columnWidths);
//
//        // Заголовки
//        table.addCell(new Cell().add("ID"));
//        table.addCell(new Cell().add("Name"));
//        table.addCell(new Cell().add("Score"));
//
//        // Данные
//        table.addCell(new Cell().add("1"));
//        table.addCell(new Cell().add("Alice"));
//        table.addCell(new Cell().add("90"));
//
//        table.addCell(new Cell().add("2"));
//        table.addCell(new Cell().add("Bob"));
//        table.addCell(new Cell().add("85"));
//
//        table.addCell(new Cell().add("3"));
//        table.addCell(new Cell().add("Charlie"));
//        table.addCell(new Cell().add("95"));
//
//        document.add(table);
//        document.close();
//
//        return new ByteArrayInputStream(outputStream.toByteArray()); // Возвращаем PDF как InputStream
//    }
//}
//
