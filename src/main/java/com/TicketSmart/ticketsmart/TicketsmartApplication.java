package com.TicketSmart.ticketsmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TicketsmartApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketsmartApplication.class, args);
	}

}
