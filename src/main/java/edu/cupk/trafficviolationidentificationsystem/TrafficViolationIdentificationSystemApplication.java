package edu.cupk.trafficviolationidentificationsystem;

import edu.cupk.trafficviolationidentificationsystem.task.WebSocketTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TrafficViolationIdentificationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrafficViolationIdentificationSystemApplication.class, args);
    }

}
