import { Component, OnInit } from '@angular/core';
import { Feedback } from '../../models/feedback.model';
import { FeedbackService } from '../../services/feedback.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-userviewfeedback',
  templateUrl: './userviewfeedback.component.html',
  styleUrls: ['./userviewfeedback.component.css']
})
export class UserviewfeedbackComponent implements OnInit {
  feedbacks: Feedback[] = [];
  feedbackToDelete: Feedback | null = null;
  showDeleteModal: boolean = false;

  constructor(
    private feedbackService: FeedbackService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadUserFeedbacks();
  }

  loadUserFeedbacks(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      this.feedbackService.getAllFeedbacksByUserId(userId).subscribe({
        next: (data) => {
          this.feedbacks = data || [];
        },
        error: () => {
          this.feedbacks = [];
        }
      });
    }
  }

  openDeleteModal(fb: Feedback): void {
    this.feedbackToDelete = fb;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.feedbackToDelete = null;
    this.showDeleteModal = false;
  }

  confirmDelete(): void {
    if (this.feedbackToDelete && this.feedbackToDelete.feedbackId) {
      this.feedbackService.deleteFeedback(this.feedbackToDelete.feedbackId).subscribe({
        next: () => {
          this.closeDeleteModal();
          this.loadUserFeedbacks();
        },
        error: () => {
          this.closeDeleteModal();
          this.loadUserFeedbacks();
        }
      });
    }
  }
}
