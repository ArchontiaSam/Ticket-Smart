package com.TicketSmart.ticketsmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TicketSmart.ticketsmart.entity.Event;
import com.TicketSmart.ticketsmart.entity.User;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findByHost(User host); // locate event by host

	@Modifying
	@Query("UPDATE Event e SET e.availableTickets = e.availableTickets - 1"
			+ "WHERE e.id = :eventId AND e.availableTickets > 0")
	int decrementIfAvailable(@Param("eventId") Long eventId);

}
