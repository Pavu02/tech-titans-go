import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  signupForm!: FormGroup;
  isSubmitted: boolean = false;
  showSuccessModal: boolean = false;
  showOtpModal: boolean = false;
  errorMessage: string = '';
  otpValue: string = '';
  otpError: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.signupForm = this.fb.group(
      {
        username: ['', Validators.required],
        email: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/)]],
        mobileNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', Validators.required]
      },
      { validators: this.passwordMatchValidator }
    );
  }

  passwordMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;
    if (password && confirmPassword && password !== confirmPassword) {
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    this.isSubmitted = true;
    this.errorMessage = '';

    if (this.signupForm.invalid) {
      return;
    }

    const email = this.signupForm.get('email')?.value;

    this.authService.requestOtp(email).subscribe({
      next: () => {
        this.showOtpModal = true;
      },
      error: (err) => {
        if (err.status === 409) {
          this.errorMessage = 'User already exists with this email';
        } else if (err.error && err.error.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Failed to send OTP. Please try again.';
        }
      }
    });
  }

  verifyOtpAndRegister(): void {
    if (!this.otpValue || this.otpValue.length !== 6) {
      this.otpError = 'Please enter a valid 6-digit OTP';
      return;
    }
    
    this.otpError = '';
    const userData = { ...this.signupForm.value, otp: this.otpValue };

    this.authService.register(userData).subscribe({
      next: () => {
        this.showOtpModal = false;
        this.showSuccessModal = true;
      },
      error: (err) => {
        if (err.status === 401) {
          this.otpError = 'Invalid or expired OTP. Please try again.';
        } else if (err.status === 409) {
           this.showOtpModal = false;
           this.errorMessage = 'User already exists with this email/mobile number';
        } else {
          this.otpError = 'Registration failed. Please try again.';
        }
      }
    });
  }

  closeOtpModal(): void {
    this.showOtpModal = false;
    this.otpValue = '';
    this.otpError = '';
  }

  onModalOk(): void {
    this.showSuccessModal = false;
    this.router.navigate(['/login']);
  }
}
