import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../../services/customer.service';
import { Customer } from '../../models/customer.model';
import { TooltipDirective } from '../../directives/tooltip.directive';
import { Subscription, interval } from 'rxjs';

@Component({
  selector: 'app-customer-queue',
  standalone: true,
  imports: [CommonModule, TooltipDirective],
  template: `
    <div class="queue-container">
      <header class="queue-header">
        <h2>Customer Call Queue</h2>
        <div class="queue-stats">
          <span class="badge badge-waiting" [appTooltip]="'Customers waiting to be connected'">
            {{ waitingCount }} Waiting
          </span>
          <button class="btn btn-success" 
                  (click)="pickNextCustomer()"
                  [disabled]="picking"
                  [appTooltip]="'Pick the next highest priority customer from the queue'">
            <span *ngIf="!picking">📞 Pick Next Customer</span>
            <span *ngIf="picking">Connecting...</span>
          </button>
        </div>
      </header>

      <div class="customer-grid" *ngIf="customers.length > 0">
        <div class="customer-card" *ngFor="let customer of customers" 
             [class.picked]="customer.callQueueStatus === 'IN_PROGRESS'">
          <div class="card-header">
            <h3>{{ customer.firstName }} {{ customer.lastName }}</h3>
            <span class="badge" 
                  [class.badge-priority-1]="customer.priorityLevel === 1"
                  [class.badge-priority-2]="customer.priorityLevel === 2"
                  [class.badge-priority-3]="customer.priorityLevel >= 3"
                  [appTooltip]="'Priority level: ' + customer.priorityLevel + ' (1 = highest)'">
              P{{ customer.priorityLevel }}
            </span>
          </div>
          <div class="card-body">
            <p [appTooltip]="'Account number'">🏦 {{ customer.accountNumber }}</p>
            <p [appTooltip]="'Email address'">📧 {{ customer.email }}</p>
            <p [appTooltip]="'Phone number'">📱 {{ customer.phone }}</p>
            <p [appTooltip]="'Customer segment'">🏷️ {{ customer.segment }}</p>
            <p [appTooltip]="'Last interaction time'">🕐 {{ customer.lastInteraction | date:'short' }}</p>
          </div>
          <div class="card-footer">
            <span class="badge" 
                  [class.badge-waiting]="customer.callQueueStatus === 'WAITING'"
                  [class.badge-in-progress]="customer.callQueueStatus === 'IN_PROGRESS'"
                  [class.badge-completed]="customer.callQueueStatus === 'COMPLETED'">
              {{ customer.callQueueStatus }}
            </span>
          </div>
        </div>
      </div>

      <div class="empty-state" *ngIf="customers.length === 0">
        <p>No customers in queue</p>
      </div>

      <div class="picked-customer" *ngIf="pickedCustomer">
        <h3>Currently Connected</h3>
        <div class="customer-card active">
          <h3>{{ pickedCustomer.firstName }} {{ pickedCustomer.lastName }}</h3>
          <p>📞 Connected at {{ now | date:'mediumTime' }}</p>
          <button class="btn btn-primary" (click)="completeCall()">Complete Call</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .queue-container { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .queue-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .queue-header h2 { font-size: 24px; color: var(--text); }
    .queue-stats { display: flex; gap: 16px; align-items: center; }
    .customer-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 16px; }
    .customer-card { background: var(--card); border-radius: var(--radius); padding: 16px; box-shadow: var(--shadow); border: 2px solid transparent; transition: all 0.2s; }
    .customer-card:hover { border-color: var(--primary); transform: translateY(-2px); }
    .customer-card.picked { border-color: var(--success); background: #f0fdf4; }
    .card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
    .card-header h3 { font-size: 16px; font-weight: 600; }
    .card-body p { font-size: 13px; color: var(--text-muted); margin-bottom: 6px; }
    .card-footer { margin-top: 12px; padding-top: 12px; border-top: 1px solid var(--border); }
    .picked-customer { margin-top: 32px; padding: 20px; background: #eff6ff; border-radius: var(--radius); }
    .picked-customer .active { border-color: var(--success); background: white; }
    .empty-state { text-align: center; padding: 60px; color: var(--text-muted); }
  `]
})
export class CustomerQueueComponent implements OnInit, OnDestroy {
  customers: Customer[] = [];
  waitingCount = 0;
  pickedCustomer: Customer | null = null;
  picking = false;
  now = new Date();
  private refreshSub!: Subscription;

  constructor(private customerService: CustomerService) {}

  ngOnInit() {
    this.loadQueue();
    this.refreshSub = interval(5000).subscribe(() => this.loadQueue());
  }

  ngOnDestroy() {
    this.refreshSub?.unsubscribe();
  }

  loadQueue() {
    this.customerService.getWaitingCustomers().subscribe({
      next: (data) => this.customers = data,
      error: (err) => console.error('Failed to load queue', err)
    });
    this.customerService.getWaitingCount().subscribe({
      next: (data) => this.waitingCount = data.waitingCount
    });
  }

  pickNextCustomer() {
    this.picking = true;
    this.customerService.pickNextCustomer().subscribe({
      next: (result) => {
        if ('message' in result) {
          alert(result.message);
        } else {
          this.pickedCustomer = result;
          this.loadQueue();
        }
        this.picking = false;
      },
      error: (err) => { console.error('Pick failed', err); this.picking = false; }
    });
  }

  completeCall() {
    if (!this.pickedCustomer) return;
    this.customerService.updateCallQueueStatus({
      customerId: this.pickedCustomer.id,
      callQueueStatus: 'COMPLETED',
      priorityLevel: this.pickedCustomer.priorityLevel,
      agentId: 'AGENT_001'
    }).subscribe(() => {
      this.pickedCustomer = null;
      this.loadQueue();
    });
  }
}
