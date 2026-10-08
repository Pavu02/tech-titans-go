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
