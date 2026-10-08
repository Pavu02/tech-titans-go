import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-usernav',
  templateUrl: './usernav.component.html',
  styleUrls: ['./usernav.component.css']
})
export class UsernavComponent implements OnInit {
  username: string = 'user';
  userRole: string = 'User';
  showLogoutModal: boolean = false;
  showBooksDropdown: boolean = false;
  showFeedbackDropdown: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.username$.subscribe((u) => {
      this.username = u || this.authService.getUsername() || 'user';
    });
    this.authService.role$.subscribe((r) => {
      this.userRole = r || this.authService.getUserRole() || 'User';
    });
  }

  toggleBooksDropdown(show: boolean): void {
    this.showBooksDropdown = show;
  }

  toggleFeedbackDropdown(show: boolean): void {
    this.showFeedbackDropdown = show;
  }

  openLogoutModal(): void {
    this.showLogoutModal = true;
  }

  closeLogoutModal(): void {
    this.showLogoutModal = false;
  }

  confirmLogout(): void {
    this.showLogoutModal = false;
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
