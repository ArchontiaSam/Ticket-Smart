package com.TicketSmart.ticketsmart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TicketSmart.ticketsmart.dto.WaitListEntryRequestDTO;
import com.TicketSmart.ticketsmart.service.WaitListEntryService;

@RestController
@RequestMapping("/api/waitList")
public class WaitListEntryController {

	private final WaitListEntryService waitListEntryService;

	public WaitListEntryController(WaitListEntryService waitListEntryService) {
		this.waitListEntryService = waitListEntryService;
	}

	@PostMapping
	public ResponseEntity<Void> joinWaitListEntry(@RequestBody WaitListEntryRequestDTO dto) {
		waitListEntryService.joinWaitList(dto.getUserId(), dto.getEventId());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> leaveWaitList(@RequestParam Long userId, @RequestParam Long eventId) {
		waitListEntryService.removeFromWaitList(userId, eventId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/event/{eventId}")
	public ResponseEntity<List<Map<String, Object>>> getWaitListEntry(@PathVariable Long eventId) {
		return ResponseEntity.ok(waitListEntryService.getWaitListEntrySorted(eventId));

	}

	@GetMapping("/score")
	public ResponseEntity<Double> getScore(@RequestParam Long userId, @RequestParam Long eventId) {
		return ResponseEntity.ok(waitListEntryService.calculatePriorityScore(userId, eventId));

	}

}
