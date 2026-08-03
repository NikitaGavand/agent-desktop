package com.agent.desktop.customer;

import com.agent.desktop.customer.model.Customer;
import com.agent.desktop.customer.model.CustomerDTO;
import com.agent.desktop.customer.model.CallQueueUpdate;
import com.agent.desktop.customer.repository.CustomerRepository;
import com.agent.desktop.customer.service.CustomerServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = Customer.builder()
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
                .build();
    }

    @Test
    void testCreateCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

        CustomerDTO dto = CustomerDTO.builder()
                .firstName("John").lastName("Doe")
                .email("john.doe@example.com").build();

        CustomerDTO result = customerService.createCustomer(dto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testGetCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));

        CustomerDTO result = customerService.getCustomer(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testGetCustomerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.getCustomer(99L));
    }

    @Test
    void testGetAllCustomers() {
        List<Customer> customers = Arrays.asList(
                sampleCustomer,
                Customer.builder().id(2L).firstName("Jane").lastName("Smith").build()
        );
        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
    }

    @Test
    void testGetWaitingCustomers() {
        List<Customer> waiting = Arrays.asList(sampleCustomer);
        when(customerRepository.findWaitingCustomersOrdered("WAITING")).thenReturn(waiting);

        List<CustomerDTO> result = customerService.getWaitingCustomers();

        assertEquals(1, result.size());
        assertEquals("WAITING", result.get(0).getCallQueueStatus());
    }

    @Test
    void testPickNextCustomer() {
        List<Customer> waiting = Arrays.asList(sampleCustomer);
        when(customerRepository.findWaitingCustomersOrdered("WAITING")).thenReturn(waiting);
        when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

        CustomerDTO result = customerService.pickNextCustomer();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testPickNextCustomerEmptyQueue() {
        when(customerRepository.findWaitingCustomersOrdered("WAITING")).thenReturn(Arrays.asList());

        CustomerDTO result = customerService.pickNextCustomer();

        assertNull(result);
    }

    @Test
    void testUpdateCallQueueStatus() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(sampleCustomer);

        CallQueueUpdate update = new CallQueueUpdate(1L, "IN_PROGRESS", 1, "AGENT_001");
        CustomerDTO result = customerService.updateCallQueueStatus(update);

        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomer() {
        doNothing().when(customerRepository).deleteById(1L);

        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetWaitingCount() {
        when(customerRepository.countByCallQueueStatus("WAITING")).thenReturn(42L);

        Long count = customerService.getWaitingCount();

        assertEquals(42L, count);
    }
}
