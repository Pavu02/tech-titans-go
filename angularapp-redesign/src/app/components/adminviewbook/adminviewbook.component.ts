import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Book } from '../../models/book.model';
import { BookService } from '../../services/book.service';

@Component({
  selector: 'app-adminviewbook',
  templateUrl: './adminviewbook.component.html',
  styleUrls: ['./adminviewbook.component.css']
})
export class AdminviewbookComponent implements OnInit {
  books: Book[] = [];
  filteredBooks: Book[] = [];
  searchTerm: string = '';
  selectedGenre: string = 'All Genres';
  genres: string[] = ['All Genres'];

  selectedCoverImage: string | null = null;
  bookToDelete: Book | null = null;
  showDeleteModal: boolean = false;

  constructor(private bookService: BookService, private router: Router) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.bookService.getAllBooks().subscribe({
      next: (data) => {
        this.books = data || [];
        this.extractGenres();
        this.applyFilter();
      },
      error: () => {
        this.books = [];
        this.filteredBooks = [];
      }
    });
  }

  extractGenres(): void {
    const genreSet = new Set<string>();
    this.books.forEach((b) => {
      if (b.genre) genreSet.add(b.genre);
    });
    this.genres = ['All Genres', ...Array.from(genreSet)];
  }

  applyFilter(): void {
    let result = [...this.books];

    if (this.searchTerm && this.searchTerm.trim() !== '') {
      const term = this.searchTerm.toLowerCase().trim();
      result = result.filter((b) => b.title && b.title.toLowerCase().includes(term));
    }

    if (this.selectedGenre && this.selectedGenre !== 'All Genres') {
      result = result.filter((b) => b.genre === this.selectedGenre);
    }

    this.filteredBooks = result;
  }

  onSearchChange(): void {
    this.applyFilter();
  }

  onGenreChange(): void {
    this.applyFilter();
  }

  onEdit(book: Book): void {
    this.router.navigate(['/adminbook'], { queryParams: { id: book.bookId } });
  }

  toggleAvailability(book: Book): void {
    if (!book.bookId) return;
    const updatedStatus = !book.isAvailable;
    const updatedBook = { ...book, isAvailable: updatedStatus };

    this.bookService.updateBook(book.bookId, updatedBook).subscribe({
      next: () => {
        book.isAvailable = updatedStatus;
        this.applyFilter();
      }
    });
  }

  showCoverImage(book: Book): void {
    this.selectedCoverImage = book.coverImage || 'assets/images/background.svg';
  }

  closeCoverImageModal(): void {
    this.selectedCoverImage = null;
  }

  openDeleteModal(book: Book): void {
    this.bookToDelete = book;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.bookToDelete = null;
    this.showDeleteModal = false;
  }

  confirmDelete(): void {
    if (this.bookToDelete && this.bookToDelete.bookId) {
      this.bookService.deleteBook(this.bookToDelete.bookId).subscribe({
        next: () => {
          this.closeDeleteModal();
          this.loadBooks();
        },
        error: () => {
          this.closeDeleteModal();
          this.loadBooks();
        }
      });
    }
  }
}
