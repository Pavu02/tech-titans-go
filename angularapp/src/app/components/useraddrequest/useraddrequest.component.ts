import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BookrentalrequestService } from '../../services/bookrentalrequest.service';
import { AuthService } from '../../services/auth.service';

export function minDateValidator(minDateStr: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return null;
    }
    return control.value < minDateStr ? { pastDate: true } : null;
  };
}

export function dateRangeValidator(): ValidatorFn {
  return (formGroup: AbstractControl): ValidationErrors | null => {
    const requestDate = formGroup.get('requestDate')?.value;
    const returnDate = formGroup.get('returnDate')?.value;
    if (requestDate && returnDate) {
      if (returnDate <= requestDate) {
        return { invalidRange: true };
      }
    }
    return null;
  };
}

@Component({
  selector: 'app-useraddrequest',
  templateUrl: './useraddrequest.component.html',
  styleUrls: ['./useraddrequest.component.css']
})
export class UseraddrequestComponent implements OnInit {
  rentalForm!: FormGroup;
  bookId: number | null = null;
  isSubmitted: boolean = false;
  showSuccessModal: boolean = false;
  generalErrorMessage: string = '';
  minDate: string = '';

  constructor(
    private fb: FormBuilder,
    private rentalService: BookrentalrequestService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    this.minDate = `${year}-${month}-${day}`;

    this.rentalForm = this.fb.group({
      requestDate: [this.minDate, [Validators.required, minDateValidator(this.minDate)]],
      returnDate: ['', [Validators.required, minDateValidator(this.minDate)]],
      comments: ['']
    }, { validators: dateRangeValidator() });

    this.route.queryParams.subscribe((params) => {
      if (params['bookId']) {
        this.bookId = Number(params['bookId']);
      }
    });
  }

  onSubmit(): void {
    this.isSubmitted = true;
    this.generalErrorMessage = '';

    if (this.rentalForm.invalid) {
      if (this.rentalForm.hasError('invalidRange')) {
        this.generalErrorMessage = 'Return Date must be strictly after the Rental Date.';
      } else if (this.rentalForm.get('returnDate')?.hasError('pastDate') || this.rentalForm.get('requestDate')?.hasError('pastDate')) {
        this.generalErrorMessage = 'Dates cannot be in the past.';
      } else {
        this.generalErrorMessage = 'All required fields must be valid';
      }
      return;
    }

    const userId = this.authService.getUserId();
    if (!userId || !this.bookId) {
      this.generalErrorMessage = 'Invalid user or book selection';
      return;
    }

    const todayStr = this.minDate;

    const requestPayload: any = {
      user: { userId: userId },
      book: { bookId: this.bookId },
      userId: userId,
      bookId: this.bookId,
      requestDate: this.rentalForm.value.requestDate,
      returnDate: this.rentalForm.value.returnDate,
      comments: this.rentalForm.value.comments || '',
      status: 'Pending'
    };

    this.rentalService.addBookRentalRequest(requestPayload).subscribe({
      next: () => {
        this.showSuccessModal = true;
      },
      error: (err) => {
        if (err.status === 400) {
          this.generalErrorMessage = (typeof err.error === 'string' ? err.error : err.error?.message) || 'A request already exists for this book';
        } else {
          this.generalErrorMessage = 'Failed to submit rental request';
        }
      }
    });
  }

  onModalOk(): void {
    this.showSuccessModal = false;
    this.router.navigate(['/userviewappliedrequest']);
  }

  goBack(): void {
    this.router.navigate(['/userviewbooks']);
  }
}
