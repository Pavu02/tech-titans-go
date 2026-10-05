import { Component, OnInit } from '@angular/core';
import { BookRentalRequest } from '../../models/book-rental-request.model';
import { BookrentalrequestService } from '../../services/bookrentalrequest.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-userviewappliedrequest',
  templateUrl: './userviewappliedrequest.component.html',
  styleUrls: ['./userviewappliedrequest.component.css']
})
export class UserviewappliedrequestComponent implements OnInit {
  requests: BookRentalRequest[] = [];
  filteredRequests: BookRentalRequest[] = [];
  searchTerm: string = '';

  selectedRequest: BookRentalRequest | null = null;
  requestToDelete: BookRentalRequest | null = null;
  showDeleteModal: boolean = false;

  constructor(
    private rentalService: BookrentalrequestService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUserRequests();
  }

  loadUserRequests(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      this.rentalService.getBookRentalRequestsByUserId(userId).subscribe({
        next: (data) => {
          this.requests = data || [];
          this.applyFilter();
        },
        error: () => {
          this.requests = [];
          this.filteredRequests = [];
        }
      });
    }
  }

  applyFilter(): void {
    if (!this.searchTerm || this.searchTerm.trim() === '') {
      this.filteredRequests = [...this.requests];
      return;
    }

    const term = this.searchTerm.toLowerCase().trim();
    this.filteredRequests = this.requests.filter(
      (r) =>
        (r.book?.title && r.book.title.toLowerCase().includes(term)) ||
        (r.bookTitle && r.bookTitle.toLowerCase().includes(term))
    );
  }

  onSearchChange(): void {
    this.applyFilter();
  }

  showMore(req: BookRentalRequest): void {
    this.selectedRequest = req;
  }

  closeDetailsModal(): void {
    this.selectedRequest = null;
  }

  openDeleteModal(req: BookRentalRequest): void {
    this.requestToDelete = req;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.requestToDelete = null;
    this.showDeleteModal = false;
  }

  confirmDelete(): void {
    if (this.requestToDelete && this.requestToDelete.rentalId) {
      this.rentalService.deleteBookRentalRequest(this.requestToDelete.rentalId).subscribe({
        next: () => {
          this.closeDeleteModal();
          this.loadUserRequests();
        },
        error: () => {
          this.closeDeleteModal();
          this.loadUserRequests();
        }
      });
    }
  }
}
