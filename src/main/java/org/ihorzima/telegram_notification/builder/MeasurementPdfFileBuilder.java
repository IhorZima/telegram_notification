package org.ihorzima.telegram_notification.builder;

import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.ihorzima.telegram_notification.model.Measurement;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.stream.Stream;

public class MeasurementPdfFileBuilder implements PdfFileBuilder<Measurement> {

    public static final String FONT_PATH = "fonts/Roboto/Roboto-Light.ttf";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    @Override
    public byte[] build(Measurement measurement) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            BaseFont baseFont = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 20, Font.BOLD, Color.BLACK);

            Font paragraphTitleFont = new Font(baseFont, 12, Font.BOLD, Color.BLACK);
            Font infoFont = new Font(baseFont, 10, Font.NORMAL, Color.BLACK);

            Font infoFontRed = new Font(baseFont, 10, Font.BOLD, Color.RED);

            titleConfigure(font, document);
            dateLocationNameSecondNameLandParagraph(measurement, paragraphTitleFont, infoFont, document);
            configurePaymentDetailsParagraph(paragraphTitleFont, document, infoFont);
            tariffCalculationParagraph(measurement, paragraphTitleFont, document, infoFont);
            amountToBePaidParagraph(measurement, paragraphTitleFont, document, infoFont);

            Paragraph paragraph7 = new Paragraph("Рахунок дійсний до кінця поточного місяця!\n" +
                    "У разі не сплати цього рахунку до кінця поточного місяця , Вам буде нараховано новий рахунок.\n" +
                    "Цей рахунок перестає діяти!", infoFontRed);
            paragraph7.setSpacingBefore(5);

            document.add(paragraph7);
            document.addHeader("header", "header");
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void amountToBePaidParagraph(Measurement measurement, Font paragraphTitleFont, Document document, Font infoFont) {
        String formattedToBePaid = formatNumber(measurement.getToBePaid());
        String currentDay = formatNumber(measurement.getCurrentDay());
        String previousDay = formatNumber(measurement.getPreviousDay());
        String currentNight = formatNumber(measurement.getCurrentNight());
        String previousNight = formatNumber(measurement.getPreviousNight());

        Paragraph paragraph4 = new Paragraph("Сума до сплати становить: " + formattedToBePaid + " грн.", paragraphTitleFont);
        paragraph4.setSpacingBefore(10);
        document.add(paragraph4);

        Paragraph paragraph5 = new Paragraph("Призначення платежу вказати", paragraphTitleFont);
        paragraph5.setSpacingBefore(10);
        document.add(paragraph5);

        Paragraph paragraph6 = new Paragraph("Сплата за електроенергію : " +
                measurement.getLandId() +
                ", " +
                measurement.getNameSecondName() +
                ", денні " + currentDay + "-" + previousDay + ", нічні " +
                currentNight + "-" + previousNight , infoFont);

        paragraph6.setSpacingBefore(10);
        document.add(paragraph6);
    }

    private void tariffCalculationParagraph(Measurement measurement, Font paragraphTitleFont, Document document, Font infoFont) {
        Paragraph paragraph3 = new Paragraph("Розрахунок", paragraphTitleFont);
        paragraph3.setSpacingBefore(5);
        document.add(paragraph3);

        String currentDay = formatNumber(measurement.getCurrentDay());
        String previousDay = formatNumber(measurement.getPreviousDay());
        String currentNight = formatNumber(measurement.getCurrentNight());
        String previousNight = formatNumber(measurement.getPreviousNight());
        String toBePaid = formatNumber(measurement.getToBePaid());

        PdfPTable table3 = new PdfPTable(5);
        table3.setWidthPercentage(100);
        table3.setSpacingBefore(20);

        addCell(table3, "Денні поточні", infoFont);
        addCell(table3, "Денні попередні", infoFont);
        addCell(table3, "Нічні поточні", infoFont);
        addCell(table3, "Нічні попередні", infoFont);
        addCell(table3, "Разом до сплати (грн)", infoFont);

        addCell(table3, currentDay, infoFont);
        addCell(table3, previousDay, infoFont);
        addCell(table3, currentNight, infoFont);
        addCell(table3, previousNight, infoFont);
        addCell(table3, toBePaid, infoFont);

        document.add(table3);
    }

    private void configurePaymentDetailsParagraph(Font paragraphTitleFont, Document document, Font infoFont) {
        PdfPTable table = new PdfPTable(2);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(50);
        table.setWidths(new float[]{2f, 6f});

        table.setSpacingBefore(20);
        table.setSpacingAfter(20);

        Paragraph paragraph2 = new Paragraph("Реквізити для сплати", paragraphTitleFont);
        paragraph2.setSpacingBefore(10);

        document.add(paragraph2);

        addCell(table, "Рахунок", infoFont);
        addCell(table, "UA603052990000026000026247033", infoFont);
        addCell(table, "МФО", infoFont);
        addCell(table, "305299", infoFont);
        addCell(table, "Банк", infoFont);
        addCell(table, "приватбанк", infoFont);
        addCell(table, "Отримувач", infoFont);
        addCell(table, "ОК СТ Злагода", infoFont);
        addCell(table, "ЄДРПОУ", infoFont);
        addCell(table, "45371936", infoFont);

        document.add(table);
    }

    private void dateLocationNameSecondNameLandParagraph(Measurement measurement, Font paragraphTitleFont, Font infoFont, Document document) {
        PdfPTable table2 = new PdfPTable(2);
        table2.setWidthPercentage(100);

        PdfPCell leftCell1 = new PdfPCell(new Phrase("Від СТ “Злагода”", paragraphTitleFont));
        PdfPCell rightCell1 = new PdfPCell(new Phrase("Місяць формування рахунку", paragraphTitleFont));

        PdfPCell leftCell2 = new PdfPCell(new Phrase("5 територія", infoFont));
        PdfPCell rightCell2 = new PdfPCell(new Phrase(measurement.getCurrentDate(), infoFont));

        Stream.of(leftCell1, rightCell1, leftCell2, rightCell2).forEach(cell -> {
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        });

        rightCell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table2.addCell(leftCell1);
        table2.addCell(rightCell1);
        table2.addCell(leftCell2);
        table2.addCell(rightCell2);

        table2.setSpacingAfter(20);

        document.add(table2);

        document.add(new Paragraph("ПІБ власника ділянки", paragraphTitleFont));
        document.add(new Paragraph(measurement.getNameSecondName(), infoFont));
        document.add(new Paragraph("Вулиця", paragraphTitleFont));
        document.add(new Paragraph(measurement.getStreet(), infoFont));
        document.add(new Paragraph("Ділянка", paragraphTitleFont));
        document.add(new Paragraph(measurement.getLandId(), infoFont));
    }

    private String formatNumber(String input) {
        input = input.replaceAll("[\\p{Zs}\\s]+", "");
        input = input.replace(',', '.');

        double value = Double.parseDouble(input);

        return DECIMAL_FORMAT.format(value);
    }

    private void titleConfigure(Font font, Document document) {
        Paragraph paragraph = new Paragraph("Рахунок-повідомлення", font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        document.add(Chunk.NEWLINE);
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(2);
        table.addCell(cell);
    }
}
