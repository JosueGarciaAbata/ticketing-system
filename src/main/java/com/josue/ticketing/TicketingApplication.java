package com.josue.ticketing;

import com.stripe.Stripe;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;


@SpringBootApplication
public class TicketingApplication {

	private static final Logger logger = Logger.getLogger(TicketingApplication.class.getName());

	public static void main(String[] args) {
		logger.info("Stripe API VERSION: " + Stripe.API_VERSION);
		SpringApplication.run(TicketingApplication.class, args);
	}

}
