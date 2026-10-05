import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BookService } from '../../services/book.service';

@Component({
  selector: 'app-adminbook',
  templateUrl: './adminbook.component.html',
  styleUrls: ['./adminbook.component.css']
})
export class AdminbookComponent implements OnInit {
  bookForm!: FormGroup;
  isEditMode: boolean = false;
  bookId: number | null = null;
  isSubmitted: boolean = false;
  showSuccessModal: boolean = false;
  successMessage: string = '';
  generalErrorMessage: string = '';
  coverImageBase64: string = '';

  constructor(
    private fb: FormBuilder,
    private bookService: BookService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.bookForm = this.fb.group({
      title: ['', Validators.required],
      author: ['', Validators.required],
      genre: ['', Validators.required],
      description: ['', Validators.required],
      rentalFee: ['', [Validators.required, Validators.min(0)]],
      coverImage: ['']
    });

    this.route.queryParams.subscribe((params) => {
      if (params['id']) {
        this.isEditMode = true;
        this.bookId = Number(params['id']);
        this.loadBookDetails(this.bookId);
      }
    });
  }

  loadBookDetails(id: number): void {
    this.bookService.getBookById(id).subscribe({
      next: (book) => {
        this.bookForm.patchValue({
          title: book.title,
          author: book.author,
          genre: book.genre,
          description: book.description,
          rentalFee: book.rentalFee
        });
        this.coverImageBase64 = book.coverImage || '';
      },
      error: () => {
        this.generalErrorMessage = 'Failed to load book details';
      }
    });
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.coverImageBase64 = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    this.isSubmitted = true;
    this.generalErrorMessage = '';

    if (this.bookForm.invalid) {
      this.generalErrorMessage = 'All fields are required';
      return;
    }

    const bookData = {
      ...this.bookForm.value,
      rentalFee: Number(this.bookForm.value.rentalFee),
      coverImage: this.coverImageBase64 || 'assets/images/background.svg',
      isAvailable: true
    };

    if (this.isEditMode && this.bookId) {
      this.bookService.updateBook(this.bookId, bookData).subscribe({
        next: () => {
          this.successMessage = 'Book Updated Successfully!';
          this.showSuccessModal = true;
        },
        error: () => {
          this.generalErrorMessage = 'Failed to update book';
        }
      });
    } else {
      this.bookService.addBook(bookData).subscribe({
        next: () => {
          this.successMessage = 'Book Added Successfully!';
          this.showSuccessModal = true;
        },
        error: () => {
          this.generalErrorMessage = 'Failed to add book';
        }
      });
    }
  }

  onModalOk(): void {
    this.showSuccessModal = false;
    this.router.navigate(['/adminviewbook']);
  }

  goBack(): void {
    this.router.navigate(['/adminviewbook']);
  }
}
