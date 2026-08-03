package com.agent.desktop.customer.service;

import com.agent.desktop.customer.model.CustomerDTO;
import com.agent.desktop.customer.model.CallQueueUpdate;

import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO dto);
    CustomerDTO getCustomer(Long id);
    List<CustomerDTO> getAllCustomers();
    List<CustomerDTO> getWaitingCustomers();
    CustomerDTO updateCustomer(Long id, CustomerDTO dto);
    CustomerDTO updateCallQueueStatus(CallQueueUpdate update);
    CustomerDTO pickNextCustomer();
    void deleteCustomer(Long id);
    Long getWaitingCount();
}
