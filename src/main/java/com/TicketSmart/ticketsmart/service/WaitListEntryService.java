package com.TicketSmart.ticketsmart.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TicketSmart.ticketsmart.entity.Event;
import com.TicketSmart.ticketsmart.entity.ReservationStatus;
import com.TicketSmart.ticketsmart.entity.User;
import com.TicketSmart.ticketsmart.entity.WaitListEntry;
import com.TicketSmart.ticketsmart.repository.EventRepository;
import com.TicketSmart.ticketsmart.repository.FriendshipRepository;
import com.TicketSmart.ticketsmart.repository.ReservationRepository;
import com.TicketSmart.ticketsmart.repository.UserRepository;
import com.TicketSmart.ticketsmart.repository.WaitListEntryRepository;

@Service
public class WaitListEntryService {

	private final UserRepository userRepository;
	private final EventRepository eventRepository;
	private final FriendshipRepository friendshipRepository;
	private final WaitListEntryRepository waitListEntryRepository;
	private final ReservationRepository reservationRepository;

	public WaitListEntryService(UserRepository userRepository, EventRepository eventRepository,
			FriendshipRepository friendshipRepository, WaitListEntryRepository waitListEntryRepository,
			ReservationRepository reservationRepository) {
		this.userRepository = userRepository;
		this.eventRepository = eventRepository;
		this.friendshipRepository = friendshipRepository;
		this.waitListEntryRepository = waitListEntryRepository;
		this.reservationRepository = reservationRepository;
	}

	@Transactional
	public void joinWaitList(Long userId, Long eventId) {

		// locate user
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		// locate event
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

		// user wants to wait when there are available tickets
		if (event.getAvailableTickets() > 0) {
			throw new RuntimeException("Tickets are currently available — no need to join the waitlist.");
		}

		// check if user already has an ACTIVE reservation for this event (cannot be in
		// the waitingList
		boolean hasActiveReservation = reservationRepository.findByEventId(eventId).stream().anyMatch(r -> r.getUser()
				.getId().equals(userId)
				&& (r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.CONFIRMED));
		if (hasActiveReservation)
			throw new RuntimeException("User already has an active reservation for this event.");

		// check if user is already in waitList
		boolean alreadyIn = waitListEntryRepository.findByEventId(eventId).stream()
				.anyMatch(w -> w.getUser().getId().equals(userId));
		if (alreadyIn)
			throw new RuntimeException("User is already in the waitlist for this event.");

		// create WaitList with user and event and save it to waitListEntryRepository
		WaitListEntry entry = new WaitListEntry();
		entry.setUser(user);
		entry.setEvent(event);
		waitListEntryRepository.save(entry);
	}

	// Score = (secondsInWaitList / 10.0) + (attendingFriendsCount * 20)
	// every user with a friend which has a CONFIRMED reservation has a 20 bonus
	@Transactional(readOnly = true)
	public double calculatePriorityScore(Long userId, Long eventId) {
		// locate user in the waitList
		WaitListEntry entry = waitListEntryRepository.findByEventId(eventId).stream()
				.filter(w -> w.getUser().getId().equals(userId)).findFirst()
				.orElseThrow(() -> new RuntimeException("User is not in the waitlist for this event."));

		// 1 to avoid division with 0 (case when it just got inserted into waitList)
		long secondsInWaitList = Math.max(1, Duration.between(entry.getJoinedAt(), LocalDateTime.now()).getSeconds());

		long attendingFriendsCount = countAttendingFriends(userId, eventId);

		return (secondsInWaitList / 10.0) + (attendingFriendsCount * 20);

	}

	public Long countAttendingFriends(Long userId, Long eventId) {
		// locate user friends ids
		Set<Long> friendIds = friendshipRepository.findAllByUserId(userId).stream()
				.map(f -> f.getUserA().getId().equals(userId) ? f.getUserB().getId() : f.getUserA().getId())
				.collect(Collectors.toSet());

		// check how many have .CONFIRMED reservations in this event
		return reservationRepository.findByEventId(eventId).stream()
				.filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
				.filter(r -> friendIds.contains(r.getUser().getId())).count();
	}

	@Transactional(readOnly = true)
	public WaitListEntry getNextInLine(Long eventId) {
		// locate user with highest score
		List<WaitListEntry> entries = waitListEntryRepository.findByEventId(eventId);

		// return null when there are no users in the list
		if (entries.isEmpty())
			return null;
		// if users have same score the ticket is given to the one who got in the
		// WaitingList first
		return entries.stream()
				.sorted(Comparator
						.comparingDouble((WaitListEntry e) -> calculatePriorityScore(e.getUser().getId(), eventId))
						.reversed().thenComparing(e -> e.getJoinedAt())) // FIFO
				.findFirst().orElse(null);
	}

	@Transactional
	public void removeFromWaitList(Long userId, Long eventId) {
		waitListEntryRepository.deleteByEventIdAndUserId(eventId, userId);
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getWaitListEntrySorted(Long eventId) {
		// locate WaitList entries
		List<WaitListEntry> entries = waitListEntryRepository.findByEventId(eventId);

		return entries.stream()
				.sorted(Comparator
						.comparingDouble((WaitListEntry e) -> calculatePriorityScore(e.getUser().getId(), eventId))
						.reversed())
				.map(e -> {
					Map<String, Object> row = new LinkedHashMap<>();
					row.put("userId", e.getUser().getId());
					row.put("userName", e.getUser().getName());
					row.put("score", calculatePriorityScore(e.getUser().getId(), eventId));
					return row;
				}).collect(Collectors.toList());
	}

	// remove all users waiting when there are no more tickets on PENDING
	@Transactional
	public void clearWaitListEntryNoMorePendingTickets(Long eventId) {
		waitListEntryRepository.deleteByEventId(eventId);
	}

}
