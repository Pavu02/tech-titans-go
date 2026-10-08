import { Component, OnInit } from '@angular/core';
import { BookRentalRequest } from '../../models/book-rental-request.model';
import { BookrentalrequestService } from '../../services/bookrentalrequest.service';

@Component({
  selector: 'app-adminviewappliedrequest',
  templateUrl: './adminviewappliedrequest.component.html',
  styleUrls: ['./adminviewappliedrequest.component.css']
})
export class AdminviewappliedrequestComponent implements OnInit {
  requests: BookRentalRequest[] = [];
  filteredRequests: BookRentalRequest[] = [];
  searchTerm: string = '';
  selectedStatus: string = 'All';
  statuses: string[] = ['All', 'Pending', 'Approved', 'Rejected', 'Returned'];

  selectedRequest: BookRentalRequest | null = null;

  constructor(private rentalService: BookrentalrequestService) { }

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.rentalService.getAllBookRentalRequests().subscribe({
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

  applyFilter(): void {
    let result = [...this.requests];

    if (this.searchTerm && this.searchTerm.trim() !== '') {
      const term = this.searchTerm.toLowerCase().trim();
      result = result.filter(
        (r) =>
          (r.book?.title && r.book.title.toLowerCase().includes(term)) ||
          (r.bookTitle && r.bookTitle.toLowerCase().includes(term))
      );
    }

    if (this.selectedStatus && this.selectedStatus !== 'All') {
      result = result.filter(
        (r) => r.status && r.status.toLowerCase() === this.selectedStatus.toLowerCase()
      );
    }

    this.filteredRequests = result;
  }

  onSearchChange(): void {
    this.applyFilter();
  }

  onStatusChange(): void {
    this.applyFilter();
  }

  updateStatus(req: BookRentalRequest, newStatus: string): void {
    if (!req.rentalId) return;
    const updated = { ...req, status: newStatus };
    this.rentalService.updateBookRentalRequest(req.rentalId, updated).subscribe({
      next: (res) => {
        req.status = newStatus;
        this.applyFilter();
      }
    });
  }

  showMore(req: BookRentalRequest): void {
    this.selectedRequest = req;
  }

  closeDetailsModal(): void {
    this.selectedRequest = null;
  }
}
