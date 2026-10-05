import { Component, OnInit } from '@angular/core';
import { Feedback } from '../../models/feedback.model';
import { FeedbackService } from '../../services/feedback.service';

@Component({
  selector: 'app-adminviewfeedback',
  templateUrl: './adminviewfeedback.component.html',
  styleUrls: ['./adminviewfeedback.component.css']
})
export class AdminviewfeedbackComponent implements OnInit {
  feedbacks: Feedback[] = [];
  selectedUser: any = null;
  showProfileModal: boolean = false;

  constructor(private feedbackService: FeedbackService) {}

  ngOnInit(): void {
    this.loadFeedbacks();
  }

  loadFeedbacks(): void {
    this.feedbackService.getFeedbacks().subscribe({
      next: (data) => {
        this.feedbacks = data || [];
      },
      error: () => {
        this.feedbacks = [];
      }
    });
  }

  showProfile(fb: Feedback): void {
    this.selectedUser = {
      username: fb.user?.username || fb.userName || 'demouser',
      email: fb.user?.email || fb.email || 'demouser@gmail.com',
      mobileNumber: fb.user?.mobileNumber || fb.mobileNumber || '1234567890'
    };
    this.showProfileModal = true;
  }

  closeProfileModal(): void {
    this.selectedUser = null;
    this.showProfileModal = false;
  }
}
