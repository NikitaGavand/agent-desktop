package com.agent.desktop.customer.controller;

import com.agent.desktop.customer.model.CustomerDTO;
import com.agent.desktop.customer.model.CallQueueUpdate;
import com.agent.desktop.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO dto) {
        log.info("Creating customer: {} {}", dto.getFirstName(), dto.getLastName());
        CustomerDTO created = customerService.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/queue/waiting")
    public ResponseEntity<List<CustomerDTO>> getWaitingCustomers() {
        return ResponseEntity.ok(customerService.getWaitingCustomers());
    }

    @GetMapping("/queue/count")
    public ResponseEntity<Map<String, Long>> getWaitingCount() {
        Map<String, Long> response = new HashMap<>();
        response.put("waitingCount", customerService.getWaitingCount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/queue/pick-next")
    public ResponseEntity<?> pickNextCustomer() {
        CustomerDTO customer = customerService.pickNextCustomer();
        if (customer == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No customers waiting in queue");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO dto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    @PostMapping("/queue/update")
    public ResponseEntity<CustomerDTO> updateCallQueueStatus(@RequestBody CallQueueUpdate update) {
        return ResponseEntity.ok(customerService.updateCallQueueStatus(update));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "customer-service");
        return ResponseEntity.ok(health);
    }
}
