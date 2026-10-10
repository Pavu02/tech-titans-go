import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './components/login/login.component';
import { SignupComponent } from './components/signup/signup.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { HomePageComponent } from './components/home-page/home-page.component';
import { AdminbookComponent } from './components/adminbook/adminbook.component';
import { AdminviewbookComponent } from './components/adminviewbook/adminviewbook.component';
import { AdminviewappliedrequestComponent } from './components/adminviewappliedrequest/adminviewappliedrequest.component';
import { AdminviewfeedbackComponent } from './components/adminviewfeedback/adminviewfeedback.component';
import { UserviewbooksComponent } from './components/userviewbooks/userviewbooks.component';
import { UseraddrequestComponent } from './components/useraddrequest/useraddrequest.component';
import { UserviewappliedrequestComponent } from './components/userviewappliedrequest/userviewappliedrequest.component';
import { UseraddfeedbackComponent } from './components/useraddfeedback/useraddfeedback.component';
import { UserviewfeedbackComponent } from './components/userviewfeedback/userviewfeedback.component';
import { ErrorComponent } from './components/error/error.component';
import { AuthGuard } from './components/authguard/auth.guard';
import { UsersuggestionsComponent } from './components/usersuggestions/usersuggestions.component';

import { AdmindashboardComponent } from './components/admindashboard/admindashboard.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },

  { path: 'homePage', component: HomePageComponent, canActivate: [AuthGuard] },
  { path: 'dashboard', redirectTo: 'homePage', pathMatch: 'full' },
  { path: 'user', redirectTo: 'homePage', pathMatch: 'full' },

  { path: 'admin-dashboard', component: AdmindashboardComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },
  { path: 'adminbook', component: AdminbookComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },
  { path: 'adminviewbook', component: AdminviewbookComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },
  { path: 'adminviewappliedrequest', component: AdminviewappliedrequestComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },
  { path: 'adminviewfeedback', component: AdminviewfeedbackComponent, canActivate: [AuthGuard], data: { role: 'Admin' } },

  { path: 'userviewbooks', component: UserviewbooksComponent, canActivate: [AuthGuard], data: { role: 'User' } },
  { path: 'useraddrequest', component: UseraddrequestComponent, canActivate: [AuthGuard], data: { role: 'User' } },
  { path: 'userviewappliedrequest', component: UserviewappliedrequestComponent, canActivate: [AuthGuard], data: { role: 'User' } },
  { path: 'useraddfeedback', component: UseraddfeedbackComponent, canActivate: [AuthGuard], data: { role: 'User' } },
  { path: 'userviewfeedback', component: UserviewfeedbackComponent, canActivate: [AuthGuard], data: { role: 'User' } },
  { path: 'usersuggestions', component: UsersuggestionsComponent, canActivate: [AuthGuard], data: { role: 'User' } },

  { path: 'error', component: ErrorComponent },
  { path: '**', redirectTo: 'error' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
