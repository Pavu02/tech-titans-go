import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {
  userRole: string | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.userRole = this.authService.getUserRole();
    this.authService.role$.subscribe((role) => {
      this.userRole = role || this.authService.getUserRole();
    });
  }

  isAdmin(): boolean {
    return this.userRole?.toLowerCase() === 'admin';
  }
}
