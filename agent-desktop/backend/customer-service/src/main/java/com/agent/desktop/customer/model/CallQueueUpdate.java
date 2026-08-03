package com.agent.desktop.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallQueueUpdate {
    private Long customerId;
    private String callQueueStatus;
    private Integer priorityLevel;
    private String agentId;
}
