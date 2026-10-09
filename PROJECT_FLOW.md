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

## 4. Migration & Environment Setup (For IAMNEO Examly Platform)
When migrating this project to the Examly platform, ensure the following environment configurations are set:

1. **Email SMTP Setup**: 
   - Ensure the application environment has `spring.mail.username` and `spring.mail.password` correctly configured for sending OTP emails.
2. **AI & Gemini API**:
   - The application uses the Google Gemini API for the Chatbot and Book Recommendation features.
   - You MUST create a `.env` file in the root of the `springapp` directory (or configure environment variables in the Examly runner) containing: `GEMINI_API_KEY=your_api_key_here`.
3. **Database Seeding**:
   - A `DatabaseSeeder.java` exists to automatically load `resources/data/booksDB.json` into the database on the first run if the `Book` table is empty.
   - It is wrapped in a fail-safe `try-catch`, ensuring the app starts gracefully even if the file is missing or seeding fails.

## Current Course of Action (Work in Progress)
- [x] Phase 1: Generated 20 diverse books in `booksDB.json` and added `DatabaseSeeder.java` with a safe fallback mechanism.
- [ ] Phase 2: Building `ChatbotController.java` to act as a secure proxy to the Gemini API, injecting the user's rental history and feedback data into the AI's context.
- [ ] Phase 3: Update Angular frontend (`chatbot.component`) to integrate with the new backend API.
- [ ] Phase 4: Create a Book Recommendations feature on the frontend using tailored prompts.
