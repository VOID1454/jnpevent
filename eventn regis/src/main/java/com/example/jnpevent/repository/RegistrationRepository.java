package com.example.jnpevent.repository;

import com.example.jnpevent.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    /**
     * Custom query to find registrations by event ID.
     *
     * @param eventId The ID of the event.
     * @return A list of registrations associated with the given event.
     */
    List<Registration> findByEventId(Long eventId);
}
