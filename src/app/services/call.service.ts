import { Injectable } from '@angular/core';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { Call, Agent, CallMetrics } from '../models/call.model';

@Injectable({
  providedIn: 'root'
})
export class CallService {
  private calls: Call[] = [];
  private agents: Agent[] = [];
  private callsSubject = new BehaviorSubject<Call[]>([]);
  private agentsSubject = new BehaviorSubject<Agent[]>([]);

  constructor() {
    this.initializeMockData();
  }

  private initializeMockData() {
    // Mock agents
    this.agents = [
      {
        id: '1',
        name: 'John Smith',
        email: 'john@company.com',
        status: 'available',
        totalCallsHandled: 45,
        averageCallDuration: 320,
        skills: ['Technical Support', 'Billing']
      },
      {
        id: '2',
        name: 'Sarah Johnson',
        email: 'sarah@company.com',
        status: 'on-call',
        currentCallId: 'call-1',
        totalCallsHandled: 38,
        averageCallDuration: 280,
        skills: ['Sales', 'Customer Service']
      },
      {
        id: '3',
        name: 'Mike Wilson',
        email: 'mike@company.com',
        status: 'available',
        totalCallsHandled: 52,
        averageCallDuration: 350,
        skills: ['Technical Support', 'Product Info']
      }
    ];

    // Mock calls in queue
    this.calls = [
      {
        id: 'call-1',
        customerName: 'Alice Brown',
        customerPhone: '+1-555-0123',
        customerEmail: 'alice@email.com',
        issue: 'Unable to login to account',
        priority: 'high',
        status: 'in-progress',
        queueTime: new Date(Date.now() - 5 * 60000),
        startTime: new Date(Date.now() - 2 * 60000),
        agentId: '2'
      },
      {
        id: 'call-2',
        customerName: 'Bob Davis',
        customerPhone: '+1-555-0124',
        issue: 'Billing inquiry',
        priority: 'medium',
        status: 'waiting',
        queueTime: new Date(Date.now() - 8 * 60000)
      },
      {
        id: 'call-3',
        customerName: 'Carol White',
        customerPhone: '+1-555-0125',
        customerEmail: 'carol@email.com',
        issue: 'Product return request',
        priority: 'low',
        status: 'waiting',
        queueTime: new Date(Date.now() - 12 * 60000)
      },
      {
        id: 'call-4',
        customerName: 'David Lee',
        customerPhone: '+1-555-0126',
        issue: 'Technical support needed',
        priority: 'urgent',
        status: 'waiting',
        queueTime: new Date(Date.now() - 3 * 60000)
      }
    ];

    this.callsSubject.next(this.calls);
    this.agentsSubject.next(this.agents);
  }

  getWaitingCalls(): Observable<Call[]> {
    return this.callsSubject.asObservable().pipe(
      map(calls => calls.filter(call => call.status === 'waiting'))
    );
  }

  getInProgressCalls(): Observable<Call[]> {
    return this.callsSubject.asObservable().pipe(
      map(calls => calls.filter(call => call.status === 'in-progress'))
    );
  }

  getAllCalls(): Observable<Call[]> {
    return this.callsSubject.asObservable();
  }

  getAgents(): Observable<Agent[]> {
    return this.agentsSubject.asObservable();
  }

  acceptCall(callId: string, agentId: string): Observable<boolean> {
    const call = this.calls.find(c => c.id === callId);
    const agent = this.agents.find(a => a.id === agentId);

    if (call && agent && agent.status === 'available') {
      call.status = 'in-progress';
      call.agentId = agentId;
      call.startTime = new Date();
      
      agent.status = 'on-call';
      agent.currentCallId = callId;
      agent.totalCallsHandled++;

      this.callsSubject.next([...this.calls]);
      this.agentsSubject.next([...this.agents]);
      
      return of(true).pipe(delay(500));
    }
    
    return of(false);
  }

  endCall(callId: string, notes?: string): Observable<boolean> {
    const call = this.calls.find(c => c.id === callId);
    
    if (call) {
      call.status = 'completed';
      call.endTime = new Date();
      call.notes = notes;
      
      if (call.startTime) {
        call.duration = Math.floor((call.endTime.getTime() - call.startTime.getTime()) / 1000);
      }

      const agent = this.agents.find(a => a.id === call.agentId);
      if (agent) {
        agent.status = 'available';
        agent.currentCallId = undefined;
        
        // Update average call duration
        if (call.duration) {
          agent.averageCallDuration = Math.floor(
            (agent.averageCallDuration * (agent.totalCallsHandled - 1) + call.duration) / agent.totalCallsHandled
          );
        }
      }

      this.callsSubject.next([...this.calls]);
      this.agentsSubject.next([...this.agents]);
      
      return of(true).pipe(delay(500));
    }
    
    return of(false);
  }

  getMetrics(): Observable<CallMetrics> {
    return this.callsSubject.asObservable().pipe(
      map(calls => {
        const totalCalls = calls.length;
        const callsInProgress = calls.filter(c => c.status === 'in-progress').length;
        const callsWaiting = calls.filter(c => c.status === 'waiting').length;
        const callsCompleted = calls.filter(c => c.status === 'completed').length;
        const callsAbandoned = calls.filter(c => c.status === 'abandoned').length;

        const completedCallsWithDuration = calls.filter(c => c.status === 'completed' && c.duration);
        const averageCallDuration = completedCallsWithDuration.length > 0
          ? completedCallsWithDuration.reduce((sum, call) => sum + (call.duration || 0), 0) / completedCallsWithDuration.length
          : 0;

        const waitingCalls = calls.filter(c => c.status === 'waiting');
        const averageWaitTime = waitingCalls.length > 0
          ? waitingCalls.reduce((sum, call) => sum + (Date.now() - call.queueTime.getTime()), 0) / waitingCalls.length / 1000
          : 0;

        const availableAgents = this.agents.filter(a => a.status === 'available').length;
        const totalAgents = this.agents.length;
        const agentUtilization = totalAgents > 0 ? ((totalAgents - availableAgents) / totalAgents) * 100 : 0;

        const serviceLevel = callsWaiting > 0 ? Math.max(0, 100 - (averageWaitTime / 60)) : 100;

        return {
          totalCalls,
          callsInProgress,
          callsWaiting,
          callsCompleted,
          callsAbandoned,
          averageWaitTime,
          averageCallDuration,
          agentUtilization,
          serviceLevel
        };
      })
    );
  }
}
