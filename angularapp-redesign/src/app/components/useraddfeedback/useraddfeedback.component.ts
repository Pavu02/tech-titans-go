import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FeedbackService } from '../../services/feedback.service';
import { AuthService } from '../../services/auth.service';

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

  constructor(
    private fb: FormBuilder,
    private feedbackService: FeedbackService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.feedbackForm = this.fb.group({
      feedbackText: ['', Validators.required]
    });
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
      feedbackText: this.feedbackForm.value.feedbackText,
      date: todayStr
    };

    this.feedbackService.sendFeedback(payload as any).subscribe({
      next: () => {
        this.showSuccessModal = true;
        this.feedbackForm.reset();
        this.isSubmitted = false;
      },
      error: () => {
        this.errorMessage = 'Failed to submit feedback. Please try again.';
      }
    });
  }

  onModalOk(): void {
    this.showSuccessModal = false;
  }
}
