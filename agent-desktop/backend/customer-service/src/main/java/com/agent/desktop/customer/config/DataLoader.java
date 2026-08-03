package com.agent.desktop.customer.config;

import com.agent.desktop.customer.model.Customer;
import com.agent.desktop.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    @Bean
    @Profile("!test")
    public CommandLineRunner loadData(CustomerRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                String[] segments = {"RETAIL", "PREMIUM", "CORPORATE", "SME"};
                String[] statuses = {"ACTIVE", "PENDING", "ACTIVE", "ACTIVE"};

                IntStream.rangeClosed(1, 100).forEach(i -> {
                    Customer customer = Customer.builder()
                            .firstName("Customer" + i)
                            .lastName("Last" + i)
                            .email("customer" + i + "@example.com")
                            .phone("+1-555-" + String.format("%04d", i))
                            .accountNumber("ACC" + String.format("%08d", i))
                            .segment(segments[i % segments.length])
                            .status(statuses[i % statuses.length])
                            .callQueueStatus("WAITING")
                            .priorityLevel((i % 5) + 1)
                            .lastInteraction(LocalDateTime.now().minusMinutes(i * 2))
                            .build();
                    repository.save(customer);
                });
                System.out.println("Loaded 100 sample customers into database");
            }
        };
    }
}
