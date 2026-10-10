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
        // Reload all requests from the backend to instantly reflect 
        // any auto-rejected competing requests for the same book.
        this.loadRequests();
      }
    });
  }

  showMore(req: BookRentalRequest): void {
    this.selectedRequest = req;
  }

  closeDetailsModal(): void {
    this.selectedRequest = null;
  }

  getRentalDays(req: BookRentalRequest): number {
    if (!req.requestDate || !req.returnDate) return 0;
    const [sy, sm, sd] = req.requestDate.split('-');
    const [ey, em, ed] = req.returnDate.split('-');
    const start = new Date(+sy, +sm - 1, +sd);
    const end = new Date(+ey, +em - 1, +ed);
    const diffTime = end.getTime() - start.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
    return diffDays > 0 ? diffDays : 1;
  }

  getEstimatedFine(req: BookRentalRequest): number {
    if (!req.returnDate || req.status?.toLowerCase() === 'returned' || req.status?.toLowerCase() === 'rejected' || req.status?.toLowerCase() === 'pending') return 0;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const [ey, em, ed] = req.returnDate.split('-');
    const returnDate = new Date(+ey, +em - 1, +ed);
    returnDate.setHours(0, 0, 0, 0);

    if (today > returnDate) {
      const diffTime = today.getTime() - returnDate.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return diffDays * 10;
    }
    return 0;
  }

  getFinalAmount(req: BookRentalRequest): number {
    const base = req.totalRentalAmount || 0;
    const fine = req.status?.toLowerCase() === 'returned' ? (req.fineAmount || 0) : this.getEstimatedFine(req);
    return base + fine;
  }
}
