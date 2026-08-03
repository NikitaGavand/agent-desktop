import { Component, OnInit } from '@angular/core';
import { CallService } from '../../../services/call.service';
import { Observable } from 'rxjs';
import { Call, Agent, CallMetrics } from '../../../models/call.model';

@Component({
  selector: 'app-agent-dashboard',
  templateUrl: './agent-dashboard.component.html',
  styleUrls: ['./agent-dashboard.component.scss']
})
export class AgentDashboardComponent implements OnInit {
  waitingCalls$: Observable<Call[]>;
  inProgressCalls$: Observable<Call[]>;
  agents$: Observable<Agent[]>;
  metrics$: Observable<CallMetrics>;

  constructor(private callService: CallService) {}

  ngOnInit(): void {
    this.waitingCalls$ = this.callService.getWaitingCalls();
    this.inProgressCalls$ = this.callService.getInProgressCalls();
    this.agents$ = this.callService.getAgents();
    this.metrics$ = this.callService.getMetrics();
  }

  acceptCall(callId: string, agentId: string): void {
    this.callService.acceptCall(callId, agentId).subscribe(success => {
      if (success) {
        console.log('Call accepted successfully');
      } else {
        console.error('Failed to accept call');
      }
    });
  }

  endCall(callId: string): void {
    const notes = prompt('Add call notes (optional):');
    this.callService.endCall(callId, notes || '').subscribe(success => {
      if (success) {
        console.log('Call ended successfully');
      } else {
        console.error('Failed to end call');
      }
    });
  }

  getPriorityClass(priority: string): string {
    switch (priority) {
      case 'urgent': return 'priority-urgent';
      case 'high': return 'priority-high';
      case 'medium': return 'priority-medium';
      case 'low': return 'priority-low';
      default: return '';
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'available': return 'status-available';
      case 'busy': return 'status-busy';
      case 'on-call': return 'status-on-call';
      case 'offline': return 'status-offline';
      default: return '';
    }
  }

  formatDuration(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  formatWaitTime(queueTime: Date): string {
    const now = new Date();
    const waitSeconds = Math.floor((now.getTime() - queueTime.getTime()) / 1000);
    return this.formatDuration(waitSeconds);
  }
}
