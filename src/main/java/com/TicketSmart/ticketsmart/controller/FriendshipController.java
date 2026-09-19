package com.TicketSmart.ticketsmart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TicketSmart.ticketsmart.dto.FriendshipRequestDTO;
import com.TicketSmart.ticketsmart.service.FriendshipService;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

	private final FriendshipService friendshipService;

	public FriendshipController(FriendshipService friendshipService) {
		this.friendshipService = friendshipService;
	}

	@PostMapping
	public ResponseEntity<Void> adddFriendship(@RequestBody FriendshipRequestDTO dto) {
		friendshipService.addFriendship(dto.getUserAId(), dto.getUserBId());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/connected")
	public ResponseEntity<Boolean> isConnected(@RequestParam Long userIdA, @RequestParam Long userIdB,
			@RequestParam(defaultValue = "3") int maxDepth) { //until a friend of a friend of a friend (3)
		boolean connected = friendshipService.isConnected(userIdA, userIdB, maxDepth);
		return ResponseEntity.ok(connected);
	}
}
