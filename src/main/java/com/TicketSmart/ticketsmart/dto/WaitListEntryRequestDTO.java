package com.TicketSmart.ticketsmart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaitListEntryRequestDTO {

	private Long userId;
	private Long eventId;
}
