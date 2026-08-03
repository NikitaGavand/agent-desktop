package com.agent.desktop.customer.service;

import com.agent.desktop.customer.model.Customer;
import com.agent.desktop.customer.model.CustomerDTO;
import com.agent.desktop.customer.model.CallQueueUpdate;
import com.agent.desktop.customer.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {
        Customer customer = mapToEntity(dto);
        Customer saved = customerRepository.save(customer);
        log.info("Customer created: id={}, email={}", saved.getId(), saved.getEmail());
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        return mapToDTO(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getWaitingCustomers() {
        return customerRepository.findWaitingCustomersOrdered("WAITING").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setSegment(dto.getSegment());
        existing.setStatus(dto.getStatus());
        Customer updated = customerRepository.save(existing);
        return mapToDTO(updated);
    }

    @Override
    public CustomerDTO updateCallQueueStatus(CallQueueUpdate update) {
        Customer customer = customerRepository.findById(update.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + update.getCustomerId()));
        customer.setCallQueueStatus(update.getCallQueueStatus());
        if (update.getPriorityLevel() != null) {
            customer.setPriorityLevel(update.getPriorityLevel());
        }
        customer.setLastInteraction(LocalDateTime.now());
        Customer updated = customerRepository.save(customer);
        log.info("Call queue updated: customerId={}, status={}, agent={}",
                update.getCustomerId(), update.getCallQueueStatus(), update.getAgentId());
        return mapToDTO(updated);
    }

    @Override
    public synchronized CustomerDTO pickNextCustomer() {
        List<Customer> waiting = customerRepository.findWaitingCustomersOrdered("WAITING");
        if (waiting.isEmpty()) {
            log.info("No waiting customers in queue");
            return null;
        }
        Customer next = waiting.get(0);
        next.setCallQueueStatus("IN_PROGRESS");
        next.setLastInteraction(LocalDateTime.now());
        Customer updated = customerRepository.save(next);
        log.info("Customer picked: id={}, name={} {}", updated.getId(), updated.getFirstName(), updated.getLastName());
        return mapToDTO(updated);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
        log.info("Customer deleted: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getWaitingCount() {
        return customerRepository.countByCallQueueStatus("WAITING");
    }

    private CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .accountNumber(customer.getAccountNumber())
                .segment(customer.getSegment())
                .status(customer.getStatus())
                .callQueueStatus(customer.getCallQueueStatus())
                .priorityLevel(customer.getPriorityLevel())
                .lastInteraction(customer.getLastInteraction())
                .createdAt(customer.getCreatedAt())
                .build();
    }

    private Customer mapToEntity(CustomerDTO dto) {
        return Customer.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .accountNumber(dto.getAccountNumber())
                .segment(dto.getSegment())
                .status(dto.getStatus())
                .build();
    }
}
