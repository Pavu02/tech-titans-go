import { Component, OnInit } from '@angular/core';
import { BookService } from '../../services/book.service';
import { AuthService } from '../../services/auth.service';
import { Book } from '../../models/book.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-usersuggestions',
  templateUrl: './usersuggestions.component.html',
  styleUrls: ['./usersuggestions.component.css']
})
export class UsersuggestionsComponent implements OnInit {
  recommendations: any[] = [];
  isLoading: boolean = true;
  error: string | null = null;

  constructor(
    private bookService: BookService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userIdStr = this.authService.getUserId();
    if (!userIdStr) {
      this.error = "User not logged in.";
      this.isLoading = false;
      return;
    }
    
    const userId = Number(userIdStr);
    
    this.bookService.getAiRecommendations(userId).subscribe({
      next: (data) => {
        this.recommendations = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = "Failed to load AI suggestions. Please try again later.";
        this.isLoading = false;
      }
    });
  }
  
  rentBook(bookId: number): void {
    this.router.navigate(['/useraddrequest'], { queryParams: { bookId: bookId } });
  }
}
