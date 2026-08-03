# Agent Desktop Application

A modern Angular-based call center agent desktop application for managing customer calls and viewing performance metrics.

## Features

### 📞 Call Management
- **Call Queue**: View and manage incoming customer calls
- **Accept Calls**: Agents can pick up calls from the waiting queue
- **Active Calls**: Monitor ongoing calls with real-time duration
- **Call Notes**: Add notes when ending calls
- **Priority System**: Visual indicators for call priority (urgent, high, medium, low)

### 👥 Agent Status
- **Real-time Status**: Track agent availability (available, busy, on-call, offline)
- **Agent Skills**: View agent specializations and expertise
- **Performance Metrics**: Individual agent call handling statistics

### 📊 Metrics Dashboard
- **Key Performance Indicators**: Total calls, waiting calls, in-progress, completed
- **Performance Metrics**: Average wait time, call duration, agent utilization
- **Service Level**: Real-time service level performance
- **Visual Analytics**: Charts and graphs for call distribution

### 🎨 Modern UI
- **Responsive Design**: Works on desktop and mobile devices
- **Real-time Updates**: Live data updates without page refresh
- **Intuitive Navigation**: Easy-to-use interface with clear sections
- **Professional Styling**: Modern gradient designs and smooth animations

## Technology Stack

- **Frontend**: Angular 17
- **Language**: TypeScript
- **Styling**: SCSS
- **Build Tool**: Angular CLI
- **State Management**: RxJS Observables

## Project Structure

```
agent-desktop/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── agent-dashboard/     # Main dashboard component
│   │   │   ├── call-queue/         # Call queue management
│   │   │   └── metrics/            # Performance metrics
│   │   ├── services/
│   │   │   └── call.service.ts     # Call management service
│   │   ├── models/
│   │   │   └── call.model.ts       # Data models
│   │   ├── app.component.ts        # Root component
│   │   └── app.module.ts           # Angular module
│   ├── styles.scss                 # Global styles
│   └── index.html                  # Main HTML
├── angular.json                    # Angular configuration
├── package.json                    # Dependencies
└── README.md                       # This file
```

## Getting Started

### Prerequisites
- Node.js (version 16 or higher)
- npm or yarn package manager

### Installation

1. **Install Dependencies**
   ```bash
   npm install
   ```

2. **Run Development Server**
   ```bash
   npm start
   ```

3. **Open Browser**
   Navigate to `http://localhost:4200`

### Build for Production

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.

## Usage

### 1. Agent Dashboard
- View real-time call queue and active calls
- Monitor agent status and availability
- Quick access to key metrics

### 2. Call Queue Management
- Accept waiting calls with a single click
- View call details (customer info, issue, priority)
- Track waiting time for each call

### 3. Active Call Handling
- Monitor call duration in real-time
- End calls and add completion notes
- Automatic agent status updates

### 4. Metrics Dashboard
- Comprehensive performance analytics
- Visual call status distribution
- Service level monitoring
- Agent utilization tracking

## Data Models

### Call
```typescript
interface Call {
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
```

### Agent
```typescript
interface Agent {
  id: string;
  name: string;
  email: string;
  status: 'available' | 'busy' | 'on-call' | 'offline';
  currentCallId?: string;
  totalCallsHandled: number;
  averageCallDuration: number;
  skills: string[];
}
```

## Features in Detail

### Real-time Updates
The application uses RxJS observables to provide real-time updates for:
- Call queue changes
- Agent status updates
- Performance metrics
- Call duration tracking

### Priority System
Calls are color-coded by priority:
- 🔴 **Urgent**: Critical issues requiring immediate attention
- 🟠 **High**: Important issues needing prompt handling
- 🟡 **Medium**: Standard priority calls
- 🟢 **Low**: Low priority or informational calls

### Performance Tracking
- **Service Level**: Percentage of calls answered within target time
- **Agent Utilization**: Percentage of agents currently busy
- **Average Wait Time**: Time customers spend in queue
- **Call Duration**: Average handling time per call

## Development

### Adding New Features
1. Create new components in `src/app/components/`
2. Add services in `src/app/services/`
3. Define data models in `src/app/models/`
4. Update routing in `app.module.ts`

### Styling Guidelines
- Use SCSS for component-specific styles
- Follow the established color scheme and design patterns
- Ensure responsive design for mobile compatibility
- Use CSS Grid and Flexbox for layouts

## Future Enhancements

- [ ] WebSocket integration for real-time updates
- [ ] Agent authentication and role-based access
- [ ] Call recording and playback
- [ ] Advanced analytics and reporting
- [ ] Integration with CRM systems
- [ ] Multi-language support
- [ ] Dark mode theme
- [ ] Mobile app version

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License.
