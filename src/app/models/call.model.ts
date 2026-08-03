export interface Call {
  id: string;
  customerName: string;
  customerPhone: string;
  customerEmail?: string;
  issue: string;
  priority: 'low' | 'medium' | 'high' | 'urgent';
  status: 'waiting' | 'in-progress' | 'completed' | 'abandoned';
  queueTime: Date;
  startTime?: Date;
  endTime?: Date;
  agentId?: string;
  duration?: number;
  notes?: string;
}

export interface Agent {
  id: string;
  name: string;
  email: string;
  status: 'available' | 'busy' | 'on-call' | 'offline';
  currentCallId?: string;
  totalCallsHandled: number;
  averageCallDuration: number;
  skills: string[];
}

export interface CallMetrics {
  totalCalls: number;
  callsInProgress: number;
  callsWaiting: number;
  callsCompleted: number;
  callsAbandoned: number;
  averageWaitTime: number;
  averageCallDuration: number;
  agentUtilization: number;
  serviceLevel: number;
}
