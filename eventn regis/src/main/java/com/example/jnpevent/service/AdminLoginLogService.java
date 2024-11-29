package com.example.jnpevent.service;

import com.example.jnpevent.model.AdminLoginLog;
import com.example.jnpevent.repository.AdminLoginLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminLoginLogService {

    @Autowired
    private AdminLoginLogRepository adminLoginLogRepository;  // Repository to interact with the DB

    // Method to get all login logs
    public List<AdminLoginLog> getAllLoginLogs() {
        return adminLoginLogRepository.findAll();  // Fetch all login logs from the database
    }
}
