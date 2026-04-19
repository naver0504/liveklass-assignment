package com.liveklass.notification.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.liveklass.notification")
public class NotificationWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationWorkerApplication.class, args);
	}
}
