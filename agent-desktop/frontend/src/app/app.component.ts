import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CustomerQueueComponent } from './components/customer-queue/customer-queue.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CustomerQueueComponent],
  template: `
    <div class="app-container">
      <nav class="navbar">
        <h1>🖥️ Agent Desktop</h1>
        <span class="version">v1.0</span>
      </nav>
      <main>
        <app-customer-queue></app-customer-queue>
      </main>
    </div>
  `,
  styles: [`
    .app-container { min-height: 100vh; }
    .navbar { background: var(--primary); color: white; padding: 16px 24px; display: flex; justify-content: space-between; align-items: center; }
    .navbar h1 { font-size: 20px; font-weight: 600; }
    .version { font-size: 12px; opacity: 0.8; }
    main { padding: 20px; }
  `]
})
export class AppComponent {}
