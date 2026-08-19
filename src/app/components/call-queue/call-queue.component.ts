import { Component, OnInit } from '@angular/core';
import { CallService } from '../../services/call.service';
import { Observable } from 'rxjs';
import { Call } from '../../models/call.model';

@Component({
  selector: 'app-call-queue',
  templateUrl: './call-queue.component.html',
  styleUrls: ['./call-queue.component.scss']
})
export class CallQueueComponent implements OnInit {
  waitingCalls$!: Observable<Call[]>;
  inProgressCalls$!: Observable<Call[]>;

  constructor(private callService: CallService) {}

  ngOnInit(): void {
    this.waitingCalls$ = this.callService.getWaitingCalls();
    this.inProgressCalls$ = this.callService.getInProgressCalls();
  }

  acceptCall(callId: string): void {
    this.callService.acceptCall(callId, '1').subscribe(success => {
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

  formatWaitTime(queueTime: Date): string {
    const now = new Date();
    const waitSeconds = Math.floor((now.getTime() - queueTime.getTime()) / 1000);
    const mins = Math.floor(waitSeconds / 60);
    const secs = waitSeconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  formatDuration(startTime: Date): string {
    const now = new Date();
    const durationSeconds = Math.floor((now.getTime() - startTime.getTime()) / 1000);
    const mins = Math.floor(durationSeconds / 60);
    const secs = durationSeconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }
}
