package com.example.assignment.repository;

import com.example.assignment.entity.User;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByAgeBetween(int minAge, int maxAge, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.age BETWEEN :minAge AND :maxAge")
    @Transactional(readOnly = true)
    Stream<User> streamByAgeBetween(@Param("minAge") int minAge, @Param("maxAge") int maxAge);

    List<User> findByAgeBetween(int minAge, int maxAge);

}
