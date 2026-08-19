import { Component, OnInit } from '@angular/core';
import { CallService } from '../../services/call.service';
import { Observable } from 'rxjs';
import { CallMetrics } from '../../models/call.model';

@Component({
  selector: 'app-metrics',
  templateUrl: './metrics.component.html',
  styleUrls: ['./metrics.component.scss']
})
export class MetricsComponent implements OnInit {
  metrics$!: Observable<CallMetrics>;

  constructor(private callService: CallService) {}

  ngOnInit(): void {
    this.metrics$ = this.callService.getMetrics();
  }

  formatDuration(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  getServiceLevelColor(serviceLevel: number): string {
    if (serviceLevel >= 80) return 'excellent';
    if (serviceLevel >= 60) return 'good';
    if (serviceLevel >= 40) return 'fair';
    return 'poor';
  }

  getUtilizationColor(utilization: number): string {
    if (utilization >= 80) return 'high';
    if (utilization >= 60) return 'medium';
    return 'low';
  }
}
