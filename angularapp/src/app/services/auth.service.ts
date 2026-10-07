import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { User } from '../models/user.model';
import { Login } from '../models/login.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public apiUrl: string = 'http://localhost:8080';

  private roleSubject = new BehaviorSubject<string | null>(localStorage.getItem('userRole'));
  public role$ = this.roleSubject.asObservable();

  private userIdSubject = new BehaviorSubject<number | null>(
    localStorage.getItem('userId') ? Number(localStorage.getItem('userId')) : null
  );
  public userId$ = this.userIdSubject.asObservable();

  private usernameSubject = new BehaviorSubject<string | null>(localStorage.getItem('username'));
  public username$ = this.usernameSubject.asObservable();

  private loggedInSubject = new BehaviorSubject<boolean>(!!localStorage.getItem('token'));
  public isLoggedIn$ = this.loggedInSubject.asObservable();

  constructor(private http: HttpClient) {}

  requestOtp(email: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/api/request-otp`, { email });
  }

  register(user: User): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/api/register`, user);
  }

  login(login: Login): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/api/login`, login).pipe(
      tap((res) => {
        if (res && res.token) {
          localStorage.setItem('token', res.token);
          if (res.userRole) {
            localStorage.setItem('userRole', res.userRole);
            this.roleSubject.next(res.userRole);
          }
          if (res.userId) {
            localStorage.setItem('userId', String(res.userId));
            this.userIdSubject.next(Number(res.userId));
          }
          if (res.username) {
            localStorage.setItem('username', res.username);
            this.usernameSubject.next(res.username);
          }
          if (res.email) {
            localStorage.setItem('email', res.email);
          }
          this.loggedInSubject.next(true);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    this.roleSubject.next(null);
    this.userIdSubject.next(null);
    this.usernameSubject.next(null);
    this.loggedInSubject.next(false);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserRole(): string | null {
    return localStorage.getItem('userRole');
  }

  getUserId(): number | null {
    const id = localStorage.getItem('userId');
    return id ? Number(id) : null;
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
