package com.example.jnpevent.controller;

import com.example.jnpevent.model.Registration;
import com.example.jnpevent.service.EventService;
import com.example.jnpevent.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private EventService eventService;




     /**
     * Displays the event registration form with a list of events.
     *
     * @param model The model to pass data to the view.
     * @return The registration form view.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registration", new Registration()); // Empty registration object
        model.addAttribute("events", eventService.getAllEvents()); // List of all events
        return "eventRegistration"; // Thymeleaf template for registration form
    }

    /**
     * Handles event registration form submission and sends email verification.
     *
     * @param registration The registration data submitted by the user.
     * @return Redirect to the verification notice page.
     */
    @PostMapping("/register/submit")
public String registerForEvent(@ModelAttribute Registration registration) {
    // Ensure that status is set to "Pending" before saving
    if (registration.getStatus() == null) {
        registration.setStatus("Pending"); // Default to "Pending" if status is not set
    }

    // Save the registration temporarily and send a verification email
    registrationService.savePendingRegistration(registration);

    // Redirect to a page instructing the user to check their email
    return "redirect:/register/verification-notice";
}

    /**
     * Displays a notification page instructing the user to check their email for verification.
     *
     * @return The verification notice view.
     */
    @GetMapping("/register/verification-notice")
    public String registrationVerificationNotice() {
        return "verification-notice"; // Page prompting the user to check their email
    }

    /**
     * Verifies the registration using the provided token.
     *
     * @param token The verification token from the email.
     * @param model The model to pass data to the view.
     * @return The success or failure verification view.
     */
    @GetMapping("/verify")
    public String verifyRegistration(@RequestParam("token") String token, Model model) {
        boolean isVerified = registrationService.verifyRegistration(token);

        if (isVerified) {
            model.addAttribute("message", "Your email has been successfully verified! Your registration is complete.");
            return "verification-success"; // Page showing successful verification
        } else {
            model.addAttribute("message", "Invalid or expired verification token. Please register again.");
            return "verification-failure"; // Page showing verification failure
        }
    }








    /**
     * Displays all events as cards and optionally the registrations for a selected event.
     *
     * @param eventId Optional event ID to filter registrations.
     * @param model   The model to pass data to the view.
     * @return The manage registrations view.
     */
    @GetMapping("/admin/manageRegistrations")
    public String manageRegistrations(
            @RequestParam(value = "eventId", required = false) Long eventId,
            Model model
    ) {
        // Add all events to the model
        model.addAttribute("events", eventService.getAllEvents());

        // If an event ID is provided, fetch registrations for that event
        if (eventId != null) {
            model.addAttribute("selectedEvent", eventService.getEventById(eventId));
            model.addAttribute("registrations", registrationService.getRegistrationsByEventId(eventId));
        }

        return "manageRegistrations"; // Thymeleaf template for registrations
    }

    /**
     * Accept a registration.
     * @param registrationId The ID of the registration to accept.
     * @return Redirects to the manage registrations page.
     */
    @PostMapping("/admin/acceptRegistration")
    public String acceptRegistration(@RequestParam("id") Long registrationId) {
        registrationService.updateRegistrationStatus(registrationId, "Accepted");
        return "redirect:/admin/manageRegistrations";
    }

    /**
     * Reject a registration.
     * @param registrationId The ID of the registration to reject.
     * @return Redirects to the manage registrations page.
     */
    @PostMapping("/admin/rejectRegistration")
    public String rejectRegistration(@RequestParam("id") Long registrationId) {
        registrationService.updateRegistrationStatus(registrationId, "Rejected");
        return "redirect:/admin/manageRegistrations";
    }
}
