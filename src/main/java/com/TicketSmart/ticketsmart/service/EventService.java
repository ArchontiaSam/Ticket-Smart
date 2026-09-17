package com.TicketSmart.ticketsmart.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TicketSmart.ticketsmart.dto.EventRequestDTO;
import com.TicketSmart.ticketsmart.dto.EventResponseDTO;
import com.TicketSmart.ticketsmart.entity.Event;
import com.TicketSmart.ticketsmart.entity.User;
import com.TicketSmart.ticketsmart.repository.EventRepository;
import com.TicketSmart.ticketsmart.repository.UserRepository;

@Service
public class EventService {

	private final EventRepository eventRepository;
	private final UserRepository userRepository;

	public EventService(EventRepository eventRepository, UserRepository userRepository) {
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
	}

	public EventResponseDTO createEvent(EventRequestDTO dto) {
		
		User host = userRepository.findById(dto.getHostId())
				.orElseThrow(() -> new RuntimeException("Host not found with id: "+dto.getHostId()));
		
		Event event =new Event();
		event.setName(dto.getName());
		event.setLocation(dto.getLocation());
        event.setTime(dto.getTime());
        event.setTotalTickets(dto.getTotalTickets());
        event.setAvailableTickets(dto.getTotalTickets());
        event.setBasePrice(dto.getBasePrice());
        event.setHost(host);
        
        Event saved = eventRepository.save(event);
        return toDTO(saved);
	}
	
	 @Transactional(readOnly = true)
	    public EventResponseDTO getEventById(Long id) {
	        Event event = eventRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
	        return toDTO(event);
	    }
	 
	  @Transactional(readOnly = true)
	    public List<EventResponseDTO> getAllEvents() {
	        List<EventResponseDTO> result = new ArrayList<>();
	        for (Event event : eventRepository.findAll()) {
	            result.add(toDTO(event));
	        }
	        return result;
	    }
	  
	
	//helper
	 private EventResponseDTO toDTO(Event e) {
	        return new EventResponseDTO(
	                e.getId(), e.getName(), e.getLocation(), e.getTime(),
	                e.getTotalTickets(), e.getAvailableTickets(), e.getBasePrice(),
	                e.getHost().getId(), e.getHost().getName()
	        );
	    }
}
