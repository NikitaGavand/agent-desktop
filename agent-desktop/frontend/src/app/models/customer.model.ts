export interface Customer {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  accountNumber: string;
  segment: string;
  status: string;
  callQueueStatus: string;
  priorityLevel: number;
  lastInteraction: string;
  createdAt: string;
}

export interface CallQueueUpdate {
  customerId: number;
  callQueueStatus: string;
  priorityLevel: number;
  agentId: string;
}
