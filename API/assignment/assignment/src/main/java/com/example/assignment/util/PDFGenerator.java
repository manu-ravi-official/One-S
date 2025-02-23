package com.example.assignment.util;

import com.example.assignment.entity.User;
import com.example.assignment.exception.BadRequestException;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

public class PDFGenerator {
    private PDFGenerator() {
    }

    public static void generatePdf(Stream<User> userStream, String outputFilePath) {
        File file = new File(outputFilePath);

        try (FileOutputStream fos = new FileOutputStream(file);
                PdfWriter writer = new PdfWriter(fos);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf)) {

            Table table = new Table(new float[] { 40f, 90f, 140f, 40f, 70f, 90f, 90f, 90f, 90f, 110f });

            String[] headers = { "ID", "Name", "Email", "Age", "Gender", "City", "State", "Country", "Phone",
                    "Signup Date" };
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setFontSize(4)));
            }

            userStream.forEach(user -> {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(user.getId())).setFontSize(4)));
                table.addCell(new Cell().add(new Paragraph(user.getName()).setFontSize(4)));
                table.addCell(new Cell().add(new Paragraph(user.getEmail()).setFontSize(4)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(user.getAge())).setFontSize(4)));
                table.addCell(new Cell().add(new Paragraph(user.getGender()).setFontSize(4)));
                table.addCell(new Cell().add(new Paragraph(user.getCity()).setFontSize(4)));
                table.addCell(new Cell().add(new Paragraph(user.getState()).setFontSize(4)));
                table.addCell(new Cell().add(new Paragraph(user.getCountry()).setFontSize(4)));
                table.addCell(new Cell().add(new Paragraph(user.getPhone()).setFontSize(4)));
                table.addCell(new Cell().add(new Paragraph(user.getSignupDate().toString()).setFontSize(4)));
            });

            document.add(table);

        } catch (IOException e) {
            throw new BadRequestException("Error generating PDF");
        }
    }
}
