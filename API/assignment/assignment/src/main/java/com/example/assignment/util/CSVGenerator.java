package com.example.assignment.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.example.assignment.entity.User;
import com.example.assignment.exception.BadRequestException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class CSVGenerator {
    private CSVGenerator() {
    }

    public static byte[] generateCsv(List<User> users) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("'ID", "Name", "Email", "Age", "Gender", "City", "State", "Country", "Phone", "Signup Date")
                     .build())) {

            for (User user : users) {
                csvPrinter.printRecord(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getAge(),
                        user.getGender(),
                        user.getCity(),
                        user.getState(),
                        user.getCountry(),
                        user.getPhone(),
                        user.getSignupDate()
                );
            }
            csvPrinter.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate CSV");
        }
    }
}