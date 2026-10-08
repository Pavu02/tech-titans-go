import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {
  userRole: string | null = null;
  stats: any = { totalBooks: 0, totalUsers: 0, totalRentals: 0 };

  constructor(private authService: AuthService, private http: HttpClient) {}

  ngOnInit(): void {
    this.userRole = this.authService.getUserRole();
    this.authService.role$.subscribe((role) => {
      this.userRole = role || this.authService.getUserRole();
    });
    this.fetchStats();
  }

  fetchStats(): void {
    this.http.get('http://localhost:8080/api/stats').subscribe({
      next: (data) => {
        this.stats = data;
      },
      error: (err) => {
        console.error('Failed to fetch stats', err);
      }
    });
  }

  isAdmin(): boolean {
    return this.userRole?.toLowerCase() === 'admin';
  }
}
