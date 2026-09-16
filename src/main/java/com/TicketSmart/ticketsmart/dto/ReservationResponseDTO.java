package com.TicketSmart.ticketsmart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.TicketSmart.ticketsmart.entity.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {

	private Long id;
	private Long userId;
	private String userName;
	private Long eventId;  		//return all essential info instead of returning Reservation entity (which consists of User,Event objects) for safety. 
	private String eventName;
	private ReservationStatus status;
	private LocalDateTime createdAt;
	private BigDecimal lockedPrice;
}
