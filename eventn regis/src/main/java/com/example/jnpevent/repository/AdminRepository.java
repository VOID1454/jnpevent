package com.example.jnpevent.repository;

import com.example.jnpevent.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username); // Custom query to find Admin by username
}
