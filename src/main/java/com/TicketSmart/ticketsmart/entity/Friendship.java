package com.TicketSmart.ticketsmart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friendships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Friendship {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_a_id", nullable = false)
	private User userA;

	@ManyToOne
	@JoinColumn(name = "user_b_id", nullable = false)
	private User userB;
}
