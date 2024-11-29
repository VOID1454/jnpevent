package com.example.jnpevent.controller;

import com.example.jnpevent.model.AdminLoginLog;
import com.example.jnpevent.model.Event;
import com.example.jnpevent.repository.AdminLoginLogRepository;
import com.example.jnpevent.service.EventService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

//logsssssss
@Autowired
private AdminLoginLogRepository adminLoginLogRepository;

@GetMapping("/logs")
public String viewLoginLogs(Model model) {
    List<AdminLoginLog> logs = adminLoginLogRepository.findAll();
    model.addAttribute("logs", logs); // Pass logs to the view
    return "logs"; // Render the logs.html template
}




    @Autowired
    private EventService eventService;

    // Show all events
    @GetMapping("/events")
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "events"; // HTML to display events list
    }

    // Show form to create a new event
    @GetMapping("/event/create")
    public String createEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "createEvent"; //  HTML for creating an event
    }

    // Save a new event
    @PostMapping("/event/create")
    public String saveEvent(@ModelAttribute("event") Event event) {
        eventService.saveEvent(event);
        return "redirect:/admin/events";
    }

    // Show form to edit an existing event
    @GetMapping("/event/edit")
    public String editEventForm(@RequestParam("id") Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "editEvent"; // HTML for editing an event
    }

    // Update an existing event
    @PostMapping("/event/edit")
    public String updateEvent(@ModelAttribute("event") Event event) {
        eventService.saveEvent(event);
        return "redirect:/admin/events";
    }

    // Delete an event
    @GetMapping("/event/delete")
    public String deleteEvent(@RequestParam("id") Long id) {
        eventService.deleteEvent(id);
        return "redirect:/admin/events";
    }

     // Mapping for /admin-home URL
     @GetMapping("/admin-home")
     public String showAdminHome() {
         return "admin-home";  //  template for the admin home page
     }

    

}
