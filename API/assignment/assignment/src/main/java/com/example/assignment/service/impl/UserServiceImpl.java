package com.example.assignment.service.impl;

import com.example.assignment.entity.User;
import com.example.assignment.exception.NoUsersFoundException;
import com.example.assignment.repository.UserRepository;
import com.example.assignment.service.UserService;
import com.example.assignment.util.CSVGenerator;
import com.example.assignment.util.PDFGenerator;
import com.example.assignment.view.UserResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<String> uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid CSV file.");
        }

        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1); // -1 preserves empty values

                if (data.length < 10) { // Ensure required fields exist
                    return ResponseEntity.badRequest().body("Invalid CSV format. Expected 10 columns.");
                }

                try {
                    Long id = data[0].trim().isEmpty() ? null : Long.parseLong(data[0].trim());
                    String name = data[1].trim();
                    String email = data[2].trim();
                    Integer age = data[3].trim().isEmpty() ? null : Integer.parseInt(data[3].trim());
                    String gender = data[4].trim();
                    String city = data[5].trim();
                    String state = data[6].trim();
                    String country = data[7].trim();
                    String phone = data[8].trim();
                    LocalDateTime signupDate = data[9].trim().isEmpty() ? null
                            : LocalDateTime.parse(data[9].trim(), DateTimeFormatter.ofPattern("dd-MM-yy H:mm"));

                    users.add(new User(id, name, email, age, gender, city, state, country, phone, signupDate));

                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("Invalid number format in CSV: " + e.getMessage());
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().body("Invalid date format in CSV: " + e.getMessage());
                }
            }

            userRepository.saveAll(users);
            return ResponseEntity.ok("CSV file uploaded and data saved successfully.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }

    @Override
    public Page<UserResponse> getUsersByAgeRange(int minAge, int maxAge, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findByAgeBetween(minAge, maxAge, pageRequest)
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getAge()));
    }

    @Override
    public byte[] exportUsersToCsv(int minAge, int maxAge) {
        List<User> users = userRepository.findByAgeBetween(minAge, maxAge);

        if (users.isEmpty()) {
            logger.warn("No users found for age range {} - {}", minAge, maxAge);
            throw new NoUsersFoundException("No users found in the given age range.");
        }

        return CSVGenerator.generateCsv(users);

    }

    @Override
    @Transactional(readOnly = true)
    public String exportUsersToPdf(int minAge, int maxAge) {
        String outputFilePath = "user_report_" + minAge + "_to_" + maxAge + ".pdf";

        List<User> users = userRepository.streamByAgeBetween(minAge, maxAge)
                .toList();
        if (users.isEmpty()) {
            throw new NoUsersFoundException("No users found in the given age range: " + minAge + " - " + maxAge);
        }

        PDFGenerator.generatePdf(users.stream(), outputFilePath);

        return outputFilePath;
    }

}
