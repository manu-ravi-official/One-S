package com.example.assignment.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.assignment.exception.BadRequestException;
import com.example.assignment.service.UserService;
import com.example.assignment.view.UserResponse;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return userService.uploadFile(file);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsersByAgeRange(
            @RequestParam(defaultValue = "0") int minAge,
            @RequestParam(defaultValue = "100") int maxAge,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserResponse> users = userService.getUsersByAgeRange(minAge, maxAge, page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportUsersToCsv(@RequestParam int minAge, @RequestParam int maxAge) {
        byte[] data = userService.exportUsersToCsv(minAge, maxAge);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
                .body(data);
    }

    @GetMapping("/pdf")
    public ResponseEntity<Resource> downloadPdf(@RequestParam int minAge, @RequestParam int maxAge) {
        String filePath = userService.exportUsersToPdf(minAge, maxAge);
        File file = new File(filePath);

        if (!file.exists()) {
            throw new BadRequestException("PDF file not found: ");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(file.length())
                .body(new FileSystemResource(file));
    }
}
