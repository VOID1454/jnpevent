package com.example.jnpevent.service;

import com.example.jnpevent.model.Registration;
import com.example.jnpevent.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EmailService emailService;

    // In-memory storage for unverified registrations
    private final Map<String, Registration> pendingRegistrations = new HashMap<>();

    /**
     * Retrieves all registrations.
     * @return List of all registrations.
     */
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    /**
     * Retrieves registrations by event ID.
     * @param eventId The ID of the event.
     * @return List of registrations for the given event.
     */
    public List<Registration> getRegistrationsByEventId(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    /**
     * Updates the status of a registration to 'Accepted' or 'Rejected'.
     * @param id The ID of the registration.
     * @param status The status to update (e.g., "Accepted", "Rejected").
     * @return The updated Registration object.
     */
    public Registration updateRegistrationStatus(Long id, String status) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found with ID: " + id));
        
        // Update the status
        registration.setStatus(status);

        // Save the updated registration
        Registration updatedRegistration = registrationRepository.save(registration);

        // Send email notification about the status change
        sendStatusEmail(registration, status);

        return updatedRegistration;
    }

    /**
     * Sends an email notification to the user when their registration status is updated.
     * @param registration The registration object.
     * @param status The new status (Accepted/Rejected).
     */
    private void sendStatusEmail(Registration registration, String status) {
        String subject = "Your Registration Status Update";
        String message = "Dear " + registration.getName() + ",\n\n" +
                "Your registration for the event '" + registration.getEvent().getName() + "' has been " + status + ".\n\n" +
                "Thank you for your interest in our event!"+ 
                "You will be mailed on status updates!";
        
        emailService.sendEmail(registration.getEmail(), subject, message);
    }
    

    /**
     * Saves a registration directly to the database.
     * @param registration The registration object to be saved.
     * @return Saved Registration object.
     */
    public Registration saveRegistration(Registration registration) {
        return registrationRepository.save(registration);
    }

    /**
     * Saves a pending registration and sends a verification email.
     * @param registration The registration details.
     * @return The generated token for email verification.
     */
    public String savePendingRegistration(Registration registration) {
        // Ensure status is set to "Pending"
        if (registration.getStatus() == null) {
            registration.setStatus("Pending");
        }
    
        // Generate a unique token
        String token = UUID.randomUUID().toString();
        pendingRegistrations.put(token, registration);
    
        // Send the verification email
        String verificationUrl = "http://localhost:8080/verify?token=" + token;
        emailService.sendEmail(
            registration.getEmail(),
            "Verify Your Registration",
            "Thank you for registering! Please verify your email by clicking the link below:\n" + verificationUrl
        );
    
        return token;
    }
    

    /**
     * Verifies a pending registration using the provided token.
     * @param token  The token to verify.
     * @return True if verification is successful, false otherwise.
     */
    public boolean verifyRegistration(String token) {
        if (pendingRegistrations.containsKey(token)) {
            // Retrieve and save the verified registration
            Registration registration = pendingRegistrations.remove(token);
            saveRegistration(registration); // Persist to database
            return true;
        }
        return false; // Token not found or expired
    }

    /**
     * Deletes a registration by its ID.
     * @param id The ID of the registration to be deleted.
     */
    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    /**
     * Retrieves a registration by its ID.
     * @param id The ID of the registration.
     * @return The Registration object if found.
     * @throws IllegalArgumentException if registration is not found.
     */
    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Registration not found with ID: " + id));
    }

    /**
     * Updates an existing registration in the database.
     * @param registration The updated registration object.
     * @return The saved Registration object.
     */
    public Registration updateRegistration(Registration registration) {
        return registrationRepository.save(registration);
    }

    /**
     * Clears all expired or unused tokens from memory.
     * This can be called periodically to free up memory.
     */
    public void clearExpiredTokens() {
        pendingRegistrations.clear();
    }
}
