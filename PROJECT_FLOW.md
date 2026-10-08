# Project Architecture & Flow

## Overview
This project is a Book Management and Rental System built with an **Angular** frontend and a **Spring Boot** backend. The system allows users to browse books, request book rentals, and provide feedback, while administrators can manage the book catalog, oversee rental requests, and review feedback.

---

## 1. Frontend Flow (Angular)
The Angular SPA (Single Page Application) is responsible for the user interface and interactions:
- **Authentication:** Users register, verify their email via OTP, and log in. Upon login, a JWT token is stored locally to maintain session state.
- **User Dashboard:** Regular users can view available books, request to rent them, view their rental history, and submit feedback.
- **Admin Dashboard:** Administrators have elevated privileges to add/edit/delete books in the catalog, approve or reject pending rental requests, and monitor user feedback.

## 2. Backend Flow (Spring Boot)
The backend provides RESTful APIs to serve the frontend requests:
- **Security:** Secured using `Spring Security` and `JWT`. Most endpoints require a valid JWT token, mapped to either a `User` or `Admin` role.
- **Database:** Connects to an **H2 in-memory database** (or MySQL) using Spring Data JPA. Entities include `User`, `Book`, `BookRentalRequest`, `Feedback`, and `OtpToken`.
- **OTP/Email Service:** Integrates with `spring-boot-starter-mail` and Gmail's SMTP server to dispatch 6-digit OTPs during the registration phase.

## 3. End-to-End Registration & OTP Flow
1. **Request OTP:** A new user enters their details on the registration page and clicks submit. The frontend hits the `/api/request-otp` endpoint with the user's email.
2. **Send Email:** The backend `OtpService` generates a 6-digit code, saves it to the `otp_tokens` table with an expiration time, and sends it via `EmailService`.
3. **Verify OTP:** The user receives the email, enters the code in the UI modal, and the frontend sends the full user details + OTP to the `/api/register` endpoint.
4. **Finalize Registration:** The backend validates the OTP against the database. If valid, the user is successfully saved to the `users` table, and the account is ready for login.
