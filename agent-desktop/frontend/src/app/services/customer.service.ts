import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer, CallQueueUpdate } from '../models/customer.model';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private apiUrl = 'http://localhost:8080/api/customers';

  constructor(private http: HttpClient) {}

  getAllCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.apiUrl);
  }

  getWaitingCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.apiUrl}/queue/waiting`);
  }

  getWaitingCount(): Observable<{ waitingCount: number }> {
    return this.http.get<{ waitingCount: number }>(`${this.apiUrl}/queue/count`);
  }

  pickNextCustomer(): Observable<Customer | { message: string }> {
    return this.http.post<Customer | { message: string }>(`${this.apiUrl}/queue/pick-next`, {});
  }

  updateCallQueueStatus(update: CallQueueUpdate): Observable<Customer> {
    return this.http.post<Customer>(`${this.apiUrl}/queue/update`, update);
  }

  getCustomer(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.apiUrl}/${id}`);
  }

  createCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(this.apiUrl, customer);
  }
}
