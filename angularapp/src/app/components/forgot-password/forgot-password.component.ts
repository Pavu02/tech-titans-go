import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {
  step: number = 1;
  emailForm!: FormGroup;
  resetForm!: FormGroup;
  
  isSubmittingEmail = false;
  isSubmittingReset = false;
  
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });

    this.resetForm = this.fb.group({
      otp: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]],
      newPassword: ['', [
        Validators.required, 
        Validators.pattern(/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
      ]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(g: FormGroup) {
    return g.get('newPassword')?.value === g.get('confirmPassword')?.value
      ? null : { mismatch: true };
  }

  requestOtp(): void {
    if (this.emailForm.invalid) {
      this.emailForm.markAllAsTouched();
      return;
    }
    
    this.isSubmittingEmail = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    const email = this.emailForm.value.email;
    
    this.authService.forgotPasswordRequestOtp(email).subscribe({
      next: (res) => {
        this.isSubmittingEmail = false;
        this.step = 2;
        this.successMessage = res.message || 'OTP sent to email (if registered)';
      },
      error: (err) => {
        this.isSubmittingEmail = false;
        this.errorMessage = err.error?.message || 'Failed to request OTP';
      }
    });
  }

  resetPassword(): void {
    if (this.resetForm.invalid) {
      this.resetForm.markAllAsTouched();
      return;
    }
    
    this.isSubmittingReset = true;
    this.errorMessage = '';
    
    const payload = {
      email: this.emailForm.value.email,
      otp: this.resetForm.value.otp,
      newPassword: this.resetForm.value.newPassword,
      confirmPassword: this.resetForm.value.confirmPassword
    };
    
    this.authService.forgotPasswordReset(payload).subscribe({
      next: (res) => {
        this.isSubmittingReset = false;
        this.successMessage = 'Password reset successfully! Redirecting to login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.isSubmittingReset = false;
        this.errorMessage = err.error?.message || 'Failed to reset password';
      }
    });
  }
}
