package com.agent.desktop.customer;

import com.agent.desktop.customer.model.Customer;
import com.agent.desktop.customer.model.CustomerDTO;
import com.agent.desktop.customer.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void testFullCustomerLifecycle() throws Exception {
        // Create customer
        CustomerDTO dto = CustomerDTO.builder()
                .firstName("Alice")
                .lastName("Wonder")
                .email("alice@example.com")
                .phone("+1-555-9999")
                .segment("PREMIUM")
                .status("ACTIVE")
                .build();

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.id").exists());

        // Get all customers
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Get waiting count (should be 1 with WAITING status)
        mockMvc.perform(get("/api/customers/queue/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waitingCount").value(1));

        // Pick next customer
        mockMvc.perform(post("/api/customers/queue/pick-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void testPickNextWithMultipleCustomers() throws Exception {
        // Create 5 customers
        for (int i = 1; i <= 5; i++) {
            Customer c = Customer.builder()
                    .firstName("Cust" + i)
                    .lastName("Last" + i)
                    .email("cust" + i + "@example.com")
                    .callQueueStatus("WAITING")
                    .priorityLevel(i)
                    .build();
            customerRepository.save(c);
        }

        // Pick next should return priority 1
        mockMvc.perform(post("/api/customers/queue/pick-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priorityLevel").value(1));

        // Pick next should return priority 2
        mockMvc.perform(post("/api/customers/queue/pick-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priorityLevel").value(2));
    }

    @Test
    void testEmptyQueuePickNext() throws Exception {
        mockMvc.perform(post("/api/customers/queue/pick-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No customers waiting in queue"));
    }
}
