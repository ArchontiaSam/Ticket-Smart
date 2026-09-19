package com.TicketSmart.ticketsmart.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipRequestDTO {

	private Long userAId;
	private Long userBId;
}
