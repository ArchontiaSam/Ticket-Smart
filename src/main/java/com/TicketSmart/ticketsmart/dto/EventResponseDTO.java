package com.TicketSmart.ticketsmart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {

		private Long id;
	    private String name;
	    private String location;
	    private LocalDateTime time;
	    private Integer totalTickets;
	    private Integer availableTickets;
	    private BigDecimal basePrice;
	    private Long hostId;
	    private String hostName;
}
