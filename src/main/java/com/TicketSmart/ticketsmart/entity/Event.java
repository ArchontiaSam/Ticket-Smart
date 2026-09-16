package com.TicketSmart.ticketsmart.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String location;

	@Column(nullable = false)
	private LocalDateTime time;

	@Column(nullable = false)
	private Integer totalTickets;

	@Column(nullable = false)
	private Integer availableTickets;

	@Column(nullable = false)
	private BigDecimal basePrice;

	@ManyToOne
	@JoinColumn(name = "host_id", nullable = false)
	private User host;

}
