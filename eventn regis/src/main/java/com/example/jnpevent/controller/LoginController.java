package com.example.jnpevent.controller;

import com.example.jnpevent.model.Admin;
import com.example.jnpevent.model.AdminLoginLog;
import com.example.jnpevent.repository.AdminRepository;
import com.example.jnpevent.repository.AdminLoginLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;

@Controller
public class LoginController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminLoginLogRepository adminLoginLogRepository;

    // Show the login page with a dynamic CAPTCHA
    @GetMapping("/login")
    public String showLoginPage(HttpSession session, Model model) {
        // Generate a random CAPTCHA
        String captchaQuestion = generateCaptchaQuestion();
        String correctAnswer = calculateCaptchaAnswer(captchaQuestion);

        // Store the CAPTCHA answer in session to compare with the user's response
        session.setAttribute("captchaAnswer", correctAnswer);

        model.addAttribute("captchaQuestion", captchaQuestion); // Send the question to the view
        return "login";
    }

    // Handle the login form submission
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("captchaAnswer") String captchaAnswer,
                        HttpSession session, HttpServletRequest request, Model model) {

        // Get the CAPTCHA answer stored in session
        String correctAnswer = (String) session.getAttribute("captchaAnswer");

        // Check CAPTCHA answer
        if (correctAnswer == null || !captchaAnswer.equals(correctAnswer)) {
            model.addAttribute("error", "Invalid CAPTCHA answer.");
            return "login"; // Return to login page if CAPTCHA fails
        }

        // Authenticate the user
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            // Store the username in the session
            session.setAttribute("username", username);

            // Get the IP address of the admin
            String ipAddress = request.getRemoteAddr();

            // Save login details to the database
            AdminLoginLog loginLog = new AdminLoginLog();
            loginLog.setUsername(username);
            loginLog.setIpAddress(ipAddress);
            loginLog.setLoginTime(LocalDateTime.now());
            adminLoginLogRepository.save(loginLog);

            // Set session timeout to 30 minutes (optional)
            session.setMaxInactiveInterval(30 * 60); // 30 minutes of inactivity

            return "redirect:/admin/home"; // Redirect to admin home page
        }

        model.addAttribute("error", "Invalid username or password.");
        return "login"; // Return to login page if authentication fails
    }

    // Logout user and invalidate session
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session on logout
        return "redirect:/login"; // Redirect to login page after logout
    }

    // Generate a random CAPTCHA question (e.g., 2 + 3, 5 - 4)
    private String generateCaptchaQuestion() {
        int num1 = (int) (Math.random() * 10); // Random number between 0 and 9
        int num2 = (int) (Math.random() * 10); // Random number between 0 and 9
        String operator = (Math.random() < 0.5) ? "+" : "-"; // Random operator: + or -

        return num1 + " " + operator + " " + num2;
    }

    // Calculate the correct answer for the CAPTCHA question
    private String calculateCaptchaAnswer(String question) {
        String[] parts = question.split(" ");
        int num1 = Integer.parseInt(parts[0]);
        int num2 = Integer.parseInt(parts[2]);
        String operator = parts[1];

        if (operator.equals("+")) {
            return String.valueOf(num1 + num2);
        } else {
            return String.valueOf(num1 - num2);
        }
    }
}
