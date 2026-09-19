package com.TicketSmart.ticketsmart.service;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TicketSmart.ticketsmart.entity.Friendship;
import com.TicketSmart.ticketsmart.entity.User;
import com.TicketSmart.ticketsmart.repository.FriendshipRepository;
import com.TicketSmart.ticketsmart.repository.UserRepository;

@Service
public class FriendshipService {

	private final FriendshipRepository friendshipRepository;
	private final UserRepository userRepository;

	public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
		this.friendshipRepository = friendshipRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public void addFriendship(Long userAId, Long userBId) {
		if (userAId.equals(userBId))
			throw new RuntimeException("A user cannot be friends with themselves.");

		// locate users
		User userA = userRepository.findById(userAId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userAId));

		User userB = userRepository.findById(userBId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userBId));

		// create friendship
		Friendship friendship = new Friendship();
		friendship.setUserA(userA);
		friendship.setUserB(userB);
		friendshipRepository.save(friendship);

	}

	// isConnected via BFS
	@Transactional
	public boolean isConnected(Long userIdA, Long userIdB, int maxDepth) {

		if (!userRepository.existsById(userIdA)) {
			throw new RuntimeException("User not found with id: " + userIdA);
		}

		if (!userRepository.existsById(userIdB)) {
			throw new RuntimeException("User not found with id: " + userIdB);
		}

		if (userIdA.equals(userIdB))
			return true; // same user

		Map<Long, List<Long>> socialGraph = buildGraph();

		if (!socialGraph.containsKey(userIdA) || !socialGraph.containsKey(userIdB))
			return false; // when there is a missing record in the friendship

		Queue<Long> queue = new LinkedList<>();
		Set<Long> visited = new HashSet<>();
		Map<Long, Integer> depthMap = new HashMap<>();

		queue.add(userIdA);
		visited.add(userIdA);
		depthMap.put(userIdA, 0);

		while (!queue.isEmpty()) {
			Long current = queue.poll();
			int currentDepth = depthMap.get(current);

			if (currentDepth >= maxDepth)
				continue; // stop when reaching maxDepth

			for (Long neighbor : socialGraph.getOrDefault(current, Collections.emptyList())) {
				if (neighbor.equals(userIdB))
					return true;

				if (!visited.contains(neighbor)) {
					visited.add(neighbor);
					depthMap.put(neighbor, currentDepth + 1);
					queue.add(neighbor);
				}
			}
		}

		return false;
	}

	// Builds adjacency list from all friendships
	// friendship is mutual (A+B=B+A)
	private Map<Long, List<Long>> buildGraph() {

		Map<Long, List<Long>> graph = new HashMap<>();

		// locate all friendships
		List<Friendship> allFriendships = friendshipRepository.findAll();

		for (Friendship f : allFriendships) {
			Long a = f.getUserA().getId();
			Long b = f.getUserB().getId();

			graph.putIfAbsent(a, new ArrayList<>());
			graph.putIfAbsent(b, new ArrayList<>());

			graph.get(a).add(b);
			graph.get(b).add(a); // mutual
		}
		return graph;
	}

}
