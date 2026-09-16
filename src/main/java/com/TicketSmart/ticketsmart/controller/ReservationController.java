package com.TicketSmart.ticketsmart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TicketSmart.ticketsmart.dto.ReservationRequestDTO;
import com.TicketSmart.ticketsmart.dto.ReservationResponseDTO;
import com.TicketSmart.ticketsmart.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

	private final ReservationService reservationService;

	public ReservationController(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@PostMapping
	public ResponseEntity<ReservationResponseDTO> tryReserveTicket(@RequestBody ReservationRequestDTO dto) {
		ReservationResponseDTO response = reservationService.tryResererveTicket(dto);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}/confirm")
	public ResponseEntity<ReservationResponseDTO> confirmReservation(@PathVariable Long id) {
		ReservationResponseDTO response = reservationService.confirmReservation(id);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}/cancel")
	public ResponseEntity<ReservationResponseDTO> cancelResevation(@PathVariable Long id) {
		ReservationResponseDTO response = reservationService.cancelReservation(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<ReservationResponseDTO>> getUserReservations(@PathVariable Long userId) {
		List<ReservationResponseDTO> response = reservationService.getUserReservations(userId);
		return ResponseEntity.ok(response);
	}
}
