import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { AgentDashboardComponent } from './components/agent-dashboard/agent-dashboard.component';
import { CallQueueComponent } from './components/call-queue/call-queue.component';
import { MetricsComponent } from './components/metrics/metrics.component';
import { CallService } from '../services/call.service';

@NgModule({
  declarations: [
    AppComponent,
    AgentDashboardComponent,
    CallQueueComponent,
    MetricsComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([
      { path: '', component: AgentDashboardComponent },
      { path: 'metrics', component: MetricsComponent },
      { path: 'queue', component: CallQueueComponent }
    ]),
    FormsModule
  ],
  providers: [CallService],
  bootstrap: [AppComponent]
})
export class AppModule { }
