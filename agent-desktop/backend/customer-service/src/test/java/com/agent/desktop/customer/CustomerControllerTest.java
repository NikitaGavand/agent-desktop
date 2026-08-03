package com.agent.desktop.customer;

import com.agent.desktop.customer.controller.CustomerController;
import com.agent.desktop.customer.model.CustomerDTO;
import com.agent.desktop.customer.model.CallQueueUpdate;
import com.agent.desktop.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDTO sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = CustomerDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1-555-0001")
                .accountNumber("ACC00000001")
                .segment("PREMIUM")
                .status("ACTIVE")
                .callQueueStatus("WAITING")
                .priorityLevel(1)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateCustomer() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(sampleCustomer);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(customerService, times(1)).createCustomer(any(CustomerDTO.class));
    }

    @Test
    void testCreateCustomerValidationError() throws Exception {
        CustomerDTO invalid = CustomerDTO.builder()
                .firstName("")
                .lastName("Doe")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCustomer() throws Exception {
        when(customerService.getCustomer(1L)).thenReturn(sampleCustomer);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(customerService, times(1)).getCustomer(1L);
    }

    @Test
    void testGetAllCustomers() throws Exception {
        List<CustomerDTO> customers = Arrays.asList(sampleCustomer,
                CustomerDTO.builder().id(2L).firstName("Jane").lastName("Smith").build());
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void testGetWaitingCustomers() throws Exception {
        List<CustomerDTO> waiting = Arrays.asList(sampleCustomer);
        when(customerService.getWaitingCustomers()).thenReturn(waiting);

        mockMvc.perform(get("/api/customers/queue/waiting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].callQueueStatus").value("WAITING"));

        verify(customerService, times(1)).getWaitingCustomers();
    }

    @Test
    void testGetWaitingCount() throws Exception {
        when(customerService.getWaitingCount()).thenReturn(42L);

        mockMvc.perform(get("/api/customers/queue/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.waitingCount").value(42));

        verify(customerService, times(1)).getWaitingCount();
    }

    @Test
    void testPickNextCustomer() throws Exception {
        when(customerService.pickNextCustomer()).thenReturn(sampleCustomer);

        mockMvc.perform(post("/api/customers/queue/pick-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.callQueueStatus").value("WAITING"));

        verify(customerService, times(1)).pickNextCustomer();
    }

    @Test
    void testPickNextCustomerEmptyQueue() throws Exception {
        when(customerService.pickNextCustomer()).thenReturn(null);

        mockMvc.perform(post("/api/customers/queue/pick-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No customers waiting in queue"));

        verify(customerService, times(1)).pickNextCustomer();
    }

    @Test
    void testUpdateCustomer() throws Exception {
        CustomerDTO updated = CustomerDTO.builder()
                .id(1L).firstName("Johnny").lastName("Doe")
                .email("johnny.doe@example.com").build();
        when(customerService.updateCustomer(eq(1L), any(CustomerDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"));

        verify(customerService, times(1)).updateCustomer(eq(1L), any(CustomerDTO.class));
    }

    @Test
    void testUpdateCallQueueStatus() throws Exception {
        CallQueueUpdate update = new CallQueueUpdate(1L, "IN_PROGRESS", 1, "AGENT_001");
        when(customerService.updateCallQueueStatus(any(CallQueueUpdate.class))).thenReturn(sampleCustomer);

        mockMvc.perform(post("/api/customers/queue/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        verify(customerService, times(1)).updateCallQueueStatus(any(CallQueueUpdate.class));
    }

    @Test
    void testDeleteCustomer() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(1L);
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/customers/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("customer-service"));
    }
}
