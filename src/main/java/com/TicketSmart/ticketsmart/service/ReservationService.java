package com.TicketSmart.ticketsmart.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TicketSmart.ticketsmart.dto.ReservationRequestDTO;
import com.TicketSmart.ticketsmart.dto.ReservationResponseDTO;
import com.TicketSmart.ticketsmart.entity.Event;
import com.TicketSmart.ticketsmart.entity.Reservation;
import com.TicketSmart.ticketsmart.entity.ReservationStatus;
import com.TicketSmart.ticketsmart.entity.User;
import com.TicketSmart.ticketsmart.repository.EventRepository;
import com.TicketSmart.ticketsmart.repository.ReservationRepository;
import com.TicketSmart.ticketsmart.repository.UserRepository;
import com.TicketSmart.ticketsmart.service.pricing.PricingStrategy;

@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final PricingStrategy pricingStrategy;

	// constructor
	public ReservationService(ReservationRepository reservationRepository, EventRepository eventRepository,
			UserRepository userRepository, PricingStrategy pricingStrategy) {
		this.reservationRepository = reservationRepository;
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
		this.pricingStrategy = pricingStrategy;

	}

	// Safe-booking
	@Transactional
	public ReservationResponseDTO tryResererveTicket(ReservationRequestDTO dto) {
		// locate user based in dto request
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
		// locate event based in dto request
		Event event = eventRepository.findById(dto.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found with id:" + dto.getEventId()));

		// db is checking if there are tickets for reservation else throw exception
		int updateRows = eventRepository.decrementIfAvailable(event.getId());

		if (updateRows == 0)
			throw new RuntimeException("No tickets available for this event.");

		//load again from db to locate  new availableTIckets
		event = eventRepository.findById(event.getId())
		        .orElseThrow(() -> new RuntimeException("Event not found after reservation"));

		
		// when there are tickets available, create one
		Reservation reservation = new Reservation();
		reservation.setUser(user);
		reservation.setEvent(event);
		reservation.setStatus(ReservationStatus.PENDING);
		reservation.setLockedPrice(pricingStrategy.calculatePrice(event));

		// save reservation
		Reservation saved = reservationRepository.save(reservation);
		return toDTO(saved);

	}

	// delete expired reservations
	@Scheduled(fixedRate = 60000) // runs every 1min
	@Transactional
	public int releaseExpiredReservations() {
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10); // user has 10min to pay or reservation is released

		List<Reservation> expired = reservationRepository.findExpiredPending(ReservationStatus.PENDING, cutoff);

		for (Reservation reservation : expired) {
			reservation.setStatus(ReservationStatus.EXPIRED);
			reservationRepository.save(reservation);

			// return available tickets
			Event event = reservation.getEvent();
			event.setAvailableTickets(event.getAvailableTickets() + 1);
			eventRepository.save(event);

			// TODO: notify first users on waitList

		}

		return expired.size();
	}

	// payment confirmation
	@Transactional
	public ReservationResponseDTO confirmReservation(Long reservationId) {
		// locate reservation
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

		// if it is expired or cancelled user cannot pay
		if (reservation.getStatus() != ReservationStatus.PENDING)
			throw new RuntimeException("Only PENDING reservations can be confirmed.");

		// after payment status changes and reservation is updated
		reservation.setStatus(ReservationStatus.CONFIRMED);
		Reservation updated = reservationRepository.save(reservation);
		return toDTO(updated);
	}

	// user cancels
	@Transactional
	public ReservationResponseDTO cancelReservation(Long reservationId) {

		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

		if (reservation.getStatus() == ReservationStatus.CONFIRMED)
			throw new RuntimeException("Cannot cancel a confirmed reservation.");

		if (reservation.getStatus() == ReservationStatus.CANCELLED)
			throw new RuntimeException("Reservation is already cancelled.");

		// if reservation is pending user can now cancel it
		reservation.setStatus(ReservationStatus.CANCELLED);
		Reservation updated = reservationRepository.save(reservation);

		// return available ticket
		Event event = reservation.getEvent();
		event.setAvailableTickets(event.getAvailableTickets() + 1);
		eventRepository.save(event);

		return toDTO(updated);
	}

	// Users reservation list
	@Transactional(readOnly = true)
	public List<ReservationResponseDTO> getUserReservations(Long userId) {
		return reservationRepository.findByUserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
	}

	// convert from entity to dto (helper function)
	private ReservationResponseDTO toDTO(Reservation r) {

		return new ReservationResponseDTO(r.getId(), r.getUser().getId(), r.getUser().getName(), r.getEvent().getId(),
				r.getEvent().getName(), r.getStatus(), r.getCreatedAt(), r.getLockedPrice());
	}

}
