import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookRentalRequest } from '../models/book-rental-request.model';

@Injectable({
  providedIn: 'root'
})
export class BookrentalrequestService {
  public apiUrl: string = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  getAllBookRentalRequests(): Observable<BookRentalRequest[]> {
    return this.http.get<BookRentalRequest[]>(`${this.apiUrl}/api/bookrentalrequest`, {
      headers: this.getHeaders()
    });
  }

  getBookRentalRequestsByUserId(userId: number): Observable<BookRentalRequest[]> {
    return this.http.get<BookRentalRequest[]>(`${this.apiUrl}/api/bookrentalrequest/user/${userId}`, {
      headers: this.getHeaders()
    });
  }

  getBookRentalRequestById(rentalId: number): Observable<BookRentalRequest> {
    return this.http.get<BookRentalRequest>(`${this.apiUrl}/api/bookrentalrequest/${rentalId}`, {
      headers: this.getHeaders()
    });
  }

  addBookRentalRequest(request: BookRentalRequest): Observable<BookRentalRequest> {
    return this.http.post<BookRentalRequest>(`${this.apiUrl}/api/bookrentalrequest`, request, {
      headers: this.getHeaders()
    });
  }

  updateBookRentalRequest(rentalId: number, request: BookRentalRequest): Observable<BookRentalRequest> {
    return this.http.put<BookRentalRequest>(`${this.apiUrl}/api/bookrentalrequest/${rentalId}`, request, {
      headers: this.getHeaders()
    });
  }

  deleteBookRentalRequest(rentalId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/bookrentalrequest/${rentalId}`, {
      headers: this.getHeaders()
    });
  }
}
