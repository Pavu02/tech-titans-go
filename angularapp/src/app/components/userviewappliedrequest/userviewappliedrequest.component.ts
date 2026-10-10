import { Component, OnInit } from '@angular/core';
import { BookRentalRequest } from '../../models/book-rental-request.model';
import { BookrentalrequestService } from '../../services/bookrentalrequest.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FeedbackService } from '../../services/feedback.service';

declare var Razorpay: any;

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
  reviewedRentalIds: Set<number> = new Set<number>();

  constructor(
    private rentalService: BookrentalrequestService,
    private authService: AuthService,
    private router: Router,
    private feedbackService: FeedbackService
  ) {}

  ngOnInit(): void {
    this.loadUserRequests();
  }

  loadUserRequests(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      this.feedbackService.getAllFeedbacksByUserId(userId).subscribe({
        next: (feedbacks) => {
          this.reviewedRentalIds = new Set((feedbacks || []).map(fb => fb.rentalId || fb.bookRentalRequest?.rentalId));
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
        },
        error: () => {
           this.rentalService.getBookRentalRequestsByUserId(userId).subscribe({
            next: (data) => {
              this.requests = data || [];
              this.applyFilter();
            }
          });
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

  giveFeedback(req: BookRentalRequest): void {
    this.router.navigate(['/useraddfeedback'], { queryParams: { rentalId: req.rentalId } });
  }

  payForRental(req: BookRentalRequest): void {
    if (req.rentalId) {
      this.rentalService.createOrder(req.rentalId).subscribe({
        next: (orderRes) => {
          if (orderRes.status === 'PAID') {
            alert('Mock Payment Successful!');
            this.loadUserRequests();
            return;
          }

          const options = {
            key: orderRes.keyId,
            amount: orderRes.amount,
            currency: orderRes.currency,
            name: 'Tech Titans Go Library',
            description: 'Book Rental Payment',
            order_id: orderRes.orderId,
            handler: (response: any) => {
              this.verifyPayment(response);
            },
            prefill: {
              name: req.user?.username || req.username || 'User',
              email: req.user?.email || 'user@example.com'
            },
            theme: {
              color: '#3f51b5'
            }
          };
          const rzp = new Razorpay(options);
          rzp.on('payment.failed', function (response: any) {
             alert('Payment Failed!');
          });
          rzp.open();
        },
        error: (err) => {
          alert(err.error?.message || 'Payment Initialization Failed!');
        }
      });
    }
  }

  verifyPayment(response: any): void {
    this.rentalService.verifyPayment(response).subscribe({
      next: (res) => {
        alert('Payment Successful!');
        this.loadUserRequests();
      },
      error: (err) => {
        alert(err.error?.message || 'Payment Verification Failed!');
      }
    });
  }

  showMore(req: BookRentalRequest): void {
    this.selectedRequest = req;
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
      return diffDays * 10; // 10 INR per day
    }
    return 0;
  }

  getFinalAmount(req: BookRentalRequest): number {
    const base = req.totalRentalAmount || 0;
    const fine = req.status?.toLowerCase() === 'returned' ? (req.fineAmount || 0) : this.getEstimatedFine(req);
    return base + fine;
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
