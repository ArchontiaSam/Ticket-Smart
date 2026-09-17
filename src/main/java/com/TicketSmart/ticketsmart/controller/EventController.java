package com.TicketSmart.ticketsmart.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TicketSmart.ticketsmart.dto.EventRequestDTO;
import com.TicketSmart.ticketsmart.dto.EventResponseDTO;
import com.TicketSmart.ticketsmart.service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {

	private final EventService eventService;

	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@PostMapping
	public ResponseEntity<EventResponseDTO> createEvent(@RequestBody EventRequestDTO dto) {
		EventResponseDTO response = eventService.createEvent(dto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
		return ResponseEntity.ok(eventService.getEventById(id));
	}

	@GetMapping
	public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
		return ResponseEntity.ok(eventService.getAllEvents());
	}
}