package com.TicketSmart.ticketsmart.service.pricing;

import java.math.BigDecimal;

import com.TicketSmart.ticketsmart.entity.Event;

public interface PricingStrategy {
	BigDecimal calculatePrice(Event event);
}
