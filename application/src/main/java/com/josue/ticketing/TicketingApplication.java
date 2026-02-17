package com.josue.ticketing;

import com.stripe.Stripe;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;
import java.util.logging.Logger;

@EnableScheduling
@SpringBootApplication
public class TicketingApplication {

	private static final Logger logger = Logger.getLogger(TicketingApplication.class.getName());

	/**
	 * Punto de entrada principal de la aplicación de ticketing.
	 * Configura la zona horaria UTC y arranca el contexto de Spring Boot.
	 * 
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		logger.info("Stripe API VERSION: " + Stripe.API_VERSION);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(TicketingApplication.class, args);
	}

}
