import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Book } from '../models/book.model';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  public apiUrl: string = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  getAllBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.apiUrl}/api/books`, { headers: this.getHeaders() });
  }

  getBookById(bookId: number): Observable<Book> {
    return this.http.get<Book>(`${this.apiUrl}/api/books/${bookId}`, { headers: this.getHeaders() });
  }

  addBook(book: Book): Observable<Book> {
    return this.http.post<Book>(`${this.apiUrl}/api/books`, book, { headers: this.getHeaders() });
  }

  updateBook(bookId: number, book: Book): Observable<Book> {
    return this.http.put<Book>(`${this.apiUrl}/api/books/${bookId}`, book, { headers: this.getHeaders() });
  }

  deleteBook(bookId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/books/${bookId}`, { headers: this.getHeaders() });
  }
}
