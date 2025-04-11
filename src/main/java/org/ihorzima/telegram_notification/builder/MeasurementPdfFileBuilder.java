package org.ihorzima.telegram_notification.builder;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.ihorzima.telegram_notification.model.Measurement;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;

@Component
public class MeasurementPdfFileBuilder implements PdfFileBuilder<Measurement> {

    @Override
    public byte[] build(Measurement measurement) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate()); // Rotate for wider tables
            PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph paragraph = new Paragraph("Показники", titleFont);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add(Chunk.NEWLINE);

            // Define table with 17 columns
            PdfPTable table = new PdfPTable(17);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(5f);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            // Header row
            // TODO: rename headers to UA
            String[] headers = {
                    "Land ID", "Previous Index", "Previous Date", "Previous Night", "Previous Day",
                    "Current Date", "Current Night", "Current Day", "Payment Date",
                    "Last Night Indicator", "Last Daily Indicator", "Last Payment Amount",
                    "Prev Night Debt (UAH)", "Prev Daily Debt (UAH)",
                    "Current Night Debt (UAH)", "Current Daily Debt (UAH)", "To Be Paid"
            };

            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
                headerCell.setBackgroundColor(Color.LIGHT_GRAY);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(headerCell);
            }

            // Data rows
            table.addCell(new Phrase(measurement.getLandId(), cellFont));
            table.addCell(new Phrase(measurement.getPreviousIndex(), cellFont));
            table.addCell(new Phrase(measurement.getPreviousDate(), cellFont));
            table.addCell(new Phrase(measurement.getPreviousNight(), cellFont));
            table.addCell(new Phrase(measurement.getPreviousDay(), cellFont));
            table.addCell(new Phrase(measurement.getCurrentDate(), cellFont));
            table.addCell(new Phrase(measurement.getCurrentNight(), cellFont));
            table.addCell(new Phrase(measurement.getCurrentDay(), cellFont));
            table.addCell(new Phrase(measurement.getPaymentDate(), cellFont));
            table.addCell(new Phrase(measurement.getLastPaymentIndicatorsNight(), cellFont));
            table.addCell(new Phrase(measurement.getLastPaymentIndicatorsDaily(), cellFont));
            table.addCell(new Phrase(measurement.getLastPaymentAmount(), cellFont));
            table.addCell(new Phrase(measurement.getTotalNightDebtForPreviousPeriodsUAH(), cellFont));
            table.addCell(new Phrase(measurement.getTotalDailyDebtForPreviousPeriodsUAH(), cellFont));
            table.addCell(new Phrase(measurement.getOvernightDebtForTheCurrentPeriodUAH(), cellFont));
            table.addCell(new Phrase(measurement.getDailyDebtForTheCurrentPeriodUAH(), cellFont));
            table.addCell(new Phrase(measurement.getToBePaid(), cellFont));

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
