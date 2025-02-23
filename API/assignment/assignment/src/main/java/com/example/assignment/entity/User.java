package com.example.assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private Integer age;
    private String gender;
    private String city;
    private String state;
    private String country;
    private String phone;

    @Column(name = "signup_date")
    private LocalDateTime signupDate;

    public User() {
    }

    public User(Long id, String name, String email, Integer age, String gender, String city,
            String state, String country, String phone, String signupDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.city = city;
        this.state = state;
        this.country = country;
        this.phone = phone;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy H:mm");
        this.signupDate = LocalDateTime.parse(signupDate, formatter);

    }
}
