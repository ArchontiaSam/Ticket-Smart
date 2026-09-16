package com.TicketSmart.ticketsmart.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {

	private Long userId;
	private Long eventId; //DTO needs only ids due to: status,time,price are been calculated from backend.
}
