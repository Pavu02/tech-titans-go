import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Book } from '../../models/book.model';
import { BookService } from '../../services/book.service';
import { BookrentalrequestService } from '../../services/bookrentalrequest.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-userviewbooks',
  templateUrl: './userviewbooks.component.html',
  styleUrls: ['./userviewbooks.component.css']
})
export class UserviewbooksComponent implements OnInit {
  books: Book[] = [];
  filteredBooks: Book[] = [];
  searchTerm: string = '';
  rentedBookIds: Set<number> = new Set<number>();
  selectedCoverImage: string | null = null;

  constructor(
    private bookService: BookService,
    private rentalService: BookrentalrequestService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserRentals();
    this.loadBooks();
  }

  loadUserRentals(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      this.rentalService.getBookRentalRequestsByUserId(userId).subscribe({
        next: (requests) => {
          this.rentedBookIds.clear();
          requests.forEach((r) => {
            const bId = r.book?.bookId || r.bookId;
            if (bId && (r.status === 'Pending' || r.status === 'Approved')) {
              this.rentedBookIds.add(bId);
            }
          });
        }
      });
    }
  }

  loadBooks(): void {
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        this.books = data || [];
        this.applyFilter();
      },
      error: () => {
        this.books = [];
        this.filteredBooks = [];
      }
    });
  }

  applyFilter(): void {
    if (!this.searchTerm || this.searchTerm.trim() === '') {
      this.filteredBooks = [...this.books];
      return;
    }

    const term = this.searchTerm.toLowerCase().trim();
    this.filteredBooks = this.books.filter(
      (b) =>
        (b.title && b.title.toLowerCase().includes(term)) ||
        (b.author && b.author.toLowerCase().includes(term)) ||
        (b.genre && b.genre.toLowerCase().includes(term))
    );
  }

  onSearchChange(): void {
    this.applyFilter();
  }

  isRented(bookId?: number): boolean {
    return !!bookId && this.rentedBookIds.has(bookId);
  }

  onRent(book: Book): void {
    if (!book.isAvailable || this.isRented(book.bookId)) return;
    this.router.navigate(['/useraddrequest'], { queryParams: { bookId: book.bookId } });
  }

  showCoverImage(book: Book): void {
    this.selectedCoverImage = book.coverImage || 'assets/images/background.svg';
  }

  closeCoverImageModal(): void {
    this.selectedCoverImage = null;
  }
}
