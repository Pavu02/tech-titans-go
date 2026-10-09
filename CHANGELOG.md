# Changelog

## [Unreleased]
### Added
- Implemented Email/OTP Registration Workflow (`OtpService`, `EmailService`, `OtpToken`, `OtpTokenRepo`).
- Added `/api/request-otp` endpoint to `AuthController` for triggering OTP emails.
- Added frontend OTP verification modal in `signup.component.html` and logic in `signup.component.ts`.
- Configured Gmail SMTP settings in `application.properties`.
- Added `spring-boot-starter-mail` dependency to `pom.xml`.

### Fixed
- Fixed Angular compilation errors by restoring missing methods (`openLogoutModal` in `adminnav.component.ts`, `requestOtp` in `auth.service.ts`).
- Fixed Spring Boot compilation error in `AuthController.java` caused by a stray character (`4package`).
- Re-added missing `spring-boot-starter-mail` dependency in `pom.xml` to resolve `JavaMailSender` not found exception.
- Updated `SecurityConfig.java` to permit unauthenticated requests to `/api/request-otp`.

### Changed
- Refactored `RegisterRequestDTO.java` to require `otp` field for validating registration requests.
- Updated UI styles globally for a modern design aesthetic.
- Redesigned Feedback UI (User and Admin views) from table layouts to a modern CSS Grid-based card layout.
- Integrated dynamic 1-5 star rating visualization in Feedback cards.
- Added display of book titles within feedback cards for better context.
- Replaced the hardcoded homepage library illustration with a lightweight CSS-based dashboard mock visual.
- Made the homepage statistics dynamic by fetching real data from the backend.
- Converted the `Genre` text input in the Admin Add Book form to a dropdown (`select`) with 10 predefined genres.
- Updated the User Book Rental Request form to allow selecting both a "Rental Date (From)" and "Return Date (To)".
- Implemented strict date range validation in the Book Rental Request form to prevent past dates and ensure the return date is strictly after the rental date.
- Added backend auto-rejection logic: when an Admin approves a book rental request, all other pending requests for the same book are automatically rejected with a comment.

### Added
- Added `rating` and `bookRentalRequest` fields to the `Feedback` model to support the new feedback features.
- Created `StatsController` backend endpoint (`/api/stats`) to serve dynamic dashboard metrics without breaking existing APIs.
