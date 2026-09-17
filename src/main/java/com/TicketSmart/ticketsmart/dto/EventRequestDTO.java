package com.TicketSmart.ticketsmart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {

	private String name;
    private String location;
    private LocalDateTime time;
    private Integer totalTickets;
    private BigDecimal basePrice;
    private Long hostId;
}
