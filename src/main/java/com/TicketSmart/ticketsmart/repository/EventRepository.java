package com.TicketSmart.ticketsmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TicketSmart.ticketsmart.entity.Event;
import com.TicketSmart.ticketsmart.entity.User;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findByHost(User host);
}
