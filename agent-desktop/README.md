# Agent Desktop — Customer Call Queue System

Full-stack application for agent desktop with customer call queue management.

## Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────────┐
│   Angular   │────▶│ API Gateway │────▶│ Customer Service│
│   (Agent    │     │  (Port 8080)│     │   (Port 8081+)  │
│   Desktop)  │◄────│             │◄────│                 │
└─────────────┘     └──────┬──────┘     └─────────────────┘
                           │
                    ┌──────┴──────┐
                    │   Eureka    │
                    │  (Port 8761)│
                    └─────────────┘
```

## Features

- **Tooltip Directive** — Hover tooltips on all customer data fields
- **REST API** — Spring Boot microservices with full CRUD
- **JUnit Tests** — Controller, service, and integration tests
- **Scalable** — Docker Compose with 3+ customer service instances, handles 100+ calls via synchronized queue
- **AWS Ready** — ECS, CloudFormation, RDS PostgreSQL configs

## Quick Start

### Backend

```bash
# Terminal 1: Eureka
cd backend/eureka-server && mvn spring-boot:run

# Terminal 2: API Gateway
cd backend/api-gateway && mvn spring-boot:run

# Terminal 3: Customer Service
cd backend/customer-service && mvn spring-boot:run
```

### Run Tests

```bash
cd backend/customer-service && mvn test
```

### Frontend

```bash
cd frontend && npm install && ng serve
```

### Docker (Production Scale)

```bash
docker-compose up --scale customer-service-1=3 --scale customer-service-2=3 --scale customer-service-3=3
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/customers | List all customers |
| GET | /api/customers/queue/waiting | Waiting customers |
| GET | /api/customers/queue/count | Queue count |
| POST | /api/customers/queue/pick-next | Pick next customer |
| POST | /api/customers/queue/update | Update call status |
| POST | /api/customers | Create customer |
| GET | /api/customers/{id} | Get customer |

## Tech Stack

- **Frontend:** Angular 17, standalone components, custom tooltip directive
- **Backend:** Java 17, Spring Boot 3.2, Spring Cloud Gateway, Eureka
- **Database:** H2 (dev), PostgreSQL (production/AWS)
- **Testing:** JUnit 5, Mockito, Spring Boot Test
- **DevOps:** Docker, Docker Compose, AWS ECS/CloudFormation
