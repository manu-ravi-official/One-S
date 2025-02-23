package com.example.assignment.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.assignment.view.UserResponse;

import org.springframework.data.domain.Page;

public interface UserService {
    ResponseEntity<String> uploadFile(MultipartFile file);

    Page<UserResponse> getUsersByAgeRange(int minAge, int maxAge, int page, int size);

    byte[] exportUsersToCsv(int minAge, int maxAge);

    String exportUsersToPdf(int minAge, int maxAge);
}
