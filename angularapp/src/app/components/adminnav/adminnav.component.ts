import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-adminnav',
  templateUrl: './adminnav.component.html',
  styleUrls: ['./adminnav.component.css']
})
export class AdminnavComponent implements OnInit {
  username: string = 'admin';
  userRole: string = 'Admin';
  showLogoutModal: boolean = false;
  showBooksDropdown: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.username$.subscribe((u) => {
      this.username = u || this.authService.getUsername() || 'admin';
    });
    this.authService.role$.subscribe((r) => {
      this.userRole = r || this.authService.getUserRole() || 'Admin';
    });
  }

  toggleBooksDropdown(show: boolean): void {
    this.showBooksDropdown = show;
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
