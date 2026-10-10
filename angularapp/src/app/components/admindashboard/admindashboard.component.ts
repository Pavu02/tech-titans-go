import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { BookrentalrequestService } from '../../services/bookrentalrequest.service';
import { FeedbackService } from '../../services/feedback.service';

@Component({
  selector: 'app-admindashboard',
  templateUrl: './admindashboard.component.html',
  styleUrls: ['./admindashboard.component.css']
})
export class AdmindashboardComponent implements OnInit {
  totalFeedbacks: number = 0;
  totalBooks: number = 0;
  activeRentals: number = 0;
  totalRevenue: number = 0;
  recentActivity: any[] = [];

  constructor(
    private bookService: BookService,
    private rentalService: BookrentalrequestService,
    private feedbackService: FeedbackService
  ) {}

  ngOnInit(): void {
    this.fetchDashboardData();
  }

  fetchDashboardData(): void {
    // 1. Fetch Books
    this.bookService.getAllBooks().subscribe((books) => {
      this.totalBooks = books.length;
    });

    // 2. Fetch Feedbacks
    this.feedbackService.getFeedbacks().subscribe((feedbacks) => {
      this.totalFeedbacks = feedbacks.length;
      
      const feedbackActivities = feedbacks.map(f => ({
        type: 'feedback',
        message: `New feedback received for `,
        highlight: f.bookRentalRequest?.book?.title || f.bookRentalRequest?.bookTitle || 'a book',
        date: new Date(f.date)
      }));
      this.mergeAndSortActivity(feedbackActivities);
    });

    // 3. Fetch Rentals
    this.rentalService.getAllBookRentalRequests().subscribe((rentals) => {
      this.activeRentals = rentals.filter(r => r.status.toLowerCase() === 'approved' || r.status.toLowerCase() === 'pending').length;
      
      this.totalRevenue = rentals
        .filter(r => r.status.toLowerCase() === 'returned' && r.totalRentalAmount)
        .reduce((sum, r) => sum + (r.totalRentalAmount || 0), 0);

      const rentalActivities = rentals.map(r => ({
        type: r.status.toLowerCase() === 'returned' ? 'return' : 'request',
        message: `User ${r.status.toLowerCase() === 'returned' ? 'returned' : 'requested'} `,
        highlight: r.book?.title || r.bookTitle || 'a book',
        date: new Date(r.requestDate)
      }));
      this.mergeAndSortActivity(rentalActivities);
    });
  }

  mergeAndSortActivity(newActivities: any[]): void {
    this.recentActivity = [...this.recentActivity, ...newActivities]
      .sort((a, b) => b.date.getTime() - a.date.getTime())
      .slice(0, 5); // keep only 5 recent
  }
}
