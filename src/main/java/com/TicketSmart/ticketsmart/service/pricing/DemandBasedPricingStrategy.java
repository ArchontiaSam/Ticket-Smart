package com.TicketSmart.ticketsmart.service.pricing;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.TicketSmart.ticketsmart.entity.Event;

@Component
public class DemandBasedPricingStrategy implements PricingStrategy {

	private static final BigDecimal LOW_AVAILABILITY_THRESHOLD = new BigDecimal("0.10"); // 10%
	private static final BigDecimal SURGE_MULTIPLIER = new BigDecimal("1.30"); // 30%

	@Override
	public BigDecimal calculatePrice(Event event) {

		BigDecimal availabilityRatio = new BigDecimal(event.getAvailableTickets())
				.divide(new BigDecimal(event.getTotalTickets()), 4, java.math.RoundingMode.HALF_UP);

		if (availabilityRatio.compareTo(LOW_AVAILABILITY_THRESHOLD) < 0)
			return event.getBasePrice().multiply(SURGE_MULTIPLIER).setScale(2, java.math.RoundingMode.HALF_UP);

		return event.getBasePrice().setScale(2, java.math.RoundingMode.HALF_UP); // round price to two decimal places
	}
}
