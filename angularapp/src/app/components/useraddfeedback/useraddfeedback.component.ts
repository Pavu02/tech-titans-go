import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FeedbackService } from '../../services/feedback.service';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { BookrentalrequestService } from '../../services/bookrentalrequest.service';
import { BookRentalRequest } from '../../models/book-rental-request.model';

@Component({
  selector: 'app-useraddfeedback',
  templateUrl: './useraddfeedback.component.html',
  styleUrls: ['./useraddfeedback.component.css']
})
export class UseraddfeedbackComponent implements OnInit {
  feedbackForm!: FormGroup;
  isSubmitted: boolean = false;
  showSuccessModal: boolean = false;
  errorMessage: string = '';
  rentalId: number | null = null;
  returnedRentals: BookRentalRequest[] = [];
  hoveredRating: number = 0;

  constructor(
    private fb: FormBuilder,
    private feedbackService: FeedbackService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private rentalService: BookrentalrequestService
  ) { }

  ngOnInit(): void {
    this.feedbackForm = this.fb.group({
      rentalId: ['', Validators.required],
      rating: ['', [Validators.required, Validators.min(1), Validators.max(5)]],
      feedbackText: ['', Validators.required]
    });

    this.route.queryParams.subscribe((params) => {
      if (params['rentalId']) {
        this.rentalId = Number(params['rentalId']);
        this.feedbackForm.patchValue({ rentalId: this.rentalId });
      }
    });

    this.loadReturnedRentals();
  }

  loadReturnedRentals(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      this.feedbackService.getAllFeedbacksByUserId(userId).subscribe({
        next: (feedbacks) => {
          const reviewedRentalIds = new Set((feedbacks || []).map(fb => fb.rentalId || fb.bookRentalRequest?.rentalId));
          
          if (this.rentalId && reviewedRentalIds.has(this.rentalId)) {
            this.errorMessage = 'Feedback already posted for this rental.';
            this.feedbackForm.patchValue({ rentalId: '' });
          }

          this.rentalService.getBookRentalRequestsByUserId(userId).subscribe({
            next: (data) => {
              this.returnedRentals = (data || []).filter(req =>
                req.status?.toLowerCase() === 'returned' && req.rentalId && !reviewedRentalIds.has(req.rentalId)
              );
            }
          });
        }
      });
    }
  }

  setRating(val: number): void {
    this.feedbackForm.patchValue({ rating: val });
  }

  onSubmit(): void {
    this.isSubmitted = true;
    this.errorMessage = '';

    if (this.feedbackForm.invalid) {
      return;
    }

    const userId = this.authService.getUserId();
    if (!userId) {
      this.errorMessage = 'User not logged in';
      return;
    }

    const todayStr = new Date().toISOString().split('T')[0];
    const payload = {
      user: { userId: userId },
      userId: userId,
      rentalId: Number(this.feedbackForm.value.rentalId),
      rating: Number(this.feedbackForm.value.rating),
      feedbackText: this.feedbackForm.value.feedbackText,
      date: todayStr
    };

    this.feedbackService.sendFeedback(payload as any).subscribe({
      next: () => {
        this.showSuccessModal = true;
        this.feedbackForm.reset();
        this.isSubmitted = false;
      },
      error: (err) => {
        if (err.error && typeof err.error === 'string') {
          this.errorMessage = err.error;
        } else if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Failed to submit feedback. Please try again.';
        }
      }
    });
  }

  onModalOk(): void {
    this.showSuccessModal = false;
    this.router.navigate(['/userviewappliedrequest']);
  }
}
