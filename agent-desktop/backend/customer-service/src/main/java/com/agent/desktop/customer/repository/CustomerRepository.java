package com.agent.desktop.customer.repository;

import com.agent.desktop.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByAccountNumber(String accountNumber);

    List<Customer> findByCallQueueStatusOrderByPriorityLevelAsc(String callQueueStatus);

    List<Customer> findByCallQueueStatusAndSegmentOrderByPriorityLevelAsc(String callQueueStatus, String segment);

    @Query("SELECT c FROM Customer c WHERE c.callQueueStatus = :status ORDER BY c.priorityLevel ASC, c.createdAt ASC")
    List<Customer> findWaitingCustomersOrdered(@Param("status") String status);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.callQueueStatus = :status")
    Long countByCallQueueStatus(@Param("status") String status);

    List<Customer> findByStatus(String status);
}
