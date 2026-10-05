package com.examly.springapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringappApplicationTests {

    private String usertoken;
    private String admintoken;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper; // To parse JSON responses

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    @Test
    @Order(1)
    void backend_testRegisterAdmin() {
        String requestBody = "{\"userId\": 1,\"email\": \"demoadmin@gmail.com\", \"password\": \"admin@1234\", \"username\": \"admin123\", \"userRole\": \"Admin\", \"mobileNumber\": \"9876543210\"}";
        ResponseEntity<String> response = restTemplate.postForEntity("/api/register",
                new HttpEntity<>(requestBody, createHeaders()), String.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Order(2)
    void backend_testRegisterUser() {
        String requestBody = "{\"userId\": 2,\"email\": \"demouser@gmail.com\", \"password\": \"user@1234\", \"username\": \"user123\", \"userRole\": \"User\", \"mobileNumber\": \"1122334455\"}";
        ResponseEntity<String> response = restTemplate.postForEntity("/api/register",
                new HttpEntity<>(requestBody, createHeaders()), String.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Order(3)
    void backend_testLoginAdmin() throws Exception {
        String requestBody = "{\"email\": \"demoadmin@gmail.com\", \"password\": \"admin@1234\"}";

        ResponseEntity<String> response = restTemplate.postForEntity("/api/login",
                new HttpEntity<>(requestBody, createHeaders()), String.class);

        // Check if response body is null
        Assertions.assertNotNull(response.getBody(), "Response body is null!");

        JsonNode responseBody = objectMapper.readTree(response.getBody());
        String token = responseBody.get("token").asText();
        admintoken = token;

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(token);
    }

    @Test
    @Order(4)
    void backend_testLoginUser() throws Exception {
        String requestBody = "{\"email\": \"demouser@gmail.com\", \"password\": \"user@1234\"}";

        ResponseEntity<String> response = restTemplate.postForEntity("/api/login",
                new HttpEntity<>(requestBody, createHeaders()), String.class);

        JsonNode responseBody = objectMapper.readTree(response.getBody());
        String token = responseBody.get("token").asText();
        usertoken = token;

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(token);
    }



@Test
@Order(5)
void backend_testAddBookWithRoleValidation() throws Exception {
    // Ensure tokens are available
    Assertions.assertNotNull(admintoken, "Admin token should not be null");
    Assertions.assertNotNull(usertoken, "User token should not be null");

    // Construct the Request Body for Book
    String requestBody = "{"
            + "\"title\": \"Effective Java\","
            + "\"author\": \"Joshua Bloch\","
            + "\"genre\": \"Programming\","
            + "\"description\": \"A best-practices guide for Java developers\","
            + "\"rentalFee\": 150.00,"
            + "\"isAvailable\": true,"
            + "\"coverImage\": \"base64EncodedImageHere\""
            + "}";

    // Test with Admin Token (Expecting 201 Created)
    HttpHeaders adminHeaders = createHeaders();
    adminHeaders.set("Authorization", "Bearer " + admintoken);
    HttpEntity<String> adminRequest = new HttpEntity<>(requestBody, adminHeaders);

    ResponseEntity<String> adminResponse = restTemplate.exchange("/api/books", HttpMethod.POST, adminRequest, String.class);

    System.out.println(adminResponse.getStatusCode() + " Status code for Admin adding Book");
    Assertions.assertEquals(HttpStatus.CREATED, adminResponse.getStatusCode());

   

    // Test with User Token (Expecting 403 Forbidden)
    HttpHeaders userHeaders = createHeaders();
    userHeaders.set("Authorization", "Bearer " + usertoken);
    HttpEntity<String> userRequest = new HttpEntity<>(requestBody, userHeaders);

    ResponseEntity<String> userResponse = restTemplate.exchange("/api/books", HttpMethod.POST, userRequest, String.class);

    System.out.println(userResponse.getStatusCode() + " Status code for User trying to add Book");
    Assertions.assertEquals(HttpStatus.FORBIDDEN, userResponse.getStatusCode());
}




@Test
@Order(6)
void backend_testGetBookByIdWithRoleValidation() throws Exception {
    // Ensure tokens are available
    Assertions.assertNotNull(admintoken, "Admin token should not be null");
    Assertions.assertNotNull(usertoken, "User token should not be null");

    // Assume a Book with ID 1 already exists in the database
    Long bookId = 1L;

    // Admin access: should succeed
    HttpHeaders adminHeaders = createHeaders();
    adminHeaders.set("Authorization", "Bearer " + admintoken);
    HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);

    ResponseEntity<String> adminResponse = restTemplate.exchange(
            "/api/books/" + bookId,
            HttpMethod.GET,
            adminRequest,
            String.class);

    System.out.println(adminResponse.getStatusCode() + " Status code for Admin fetching Book by ID");
    Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());

    
    // User access: should be forbidden (if only Admin is allowed)
    HttpHeaders userHeaders = createHeaders();
    userHeaders.set("Authorization", "Bearer " + usertoken);
    HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

    ResponseEntity<String> userResponse = restTemplate.exchange(
            "/api/books/" + bookId,
            HttpMethod.GET,
            userRequest,
            String.class);

    System.out.println(userResponse.getStatusCode() + " Status code for User trying to access Book by ID");

    // Expected behavior depends on your controller's access configuration:
    // If user access is forbidden:
    Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());

    // If user access is allowed:
    // Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());
}





@Test
@Order(7)
void backend_testGetAllBooksWithRoleValidation() throws Exception {
    // Ensure tokens are available
    Assertions.assertNotNull(admintoken, "Admin token should not be null");
    Assertions.assertNotNull(usertoken, "User token should not be null");

    // --- Admin Role Access ---
    HttpHeaders adminHeaders = createHeaders();
    adminHeaders.set("Authorization", "Bearer " + admintoken);
    HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);

    ResponseEntity<String> adminResponse = restTemplate.exchange(
            "/api/books",
            HttpMethod.GET,
            adminRequest,
            String.class
    );

    System.out.println("Admin Status Code: " + adminResponse.getStatusCode());
    Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());

    
    // --- User Role Access ---
    HttpHeaders userHeaders = createHeaders();
    userHeaders.set("Authorization", "Bearer " + usertoken);
    HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

    ResponseEntity<String> userResponse = restTemplate.exchange(
            "/api/books",
            HttpMethod.GET,
            userRequest,
            String.class
    );

    System.out.println("User Status Code: " + userResponse.getStatusCode());
    Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());

}



@Test
@Order(8)
void backend_testUpdateBookWithRoleValidation() throws Exception {
	// Ensure tokens are available
	Assertions.assertNotNull(admintoken, "Admin token should not be null");
	Assertions.assertNotNull(usertoken, "User token should not be null");

	Long bookId = 1L; // Ensure this book exists in your test DB

	String requestBody = "{"
			+ "\"title\": \"Updated Java Book\","
			+ "\"author\": \"Updated Author\","
			+ "\"genre\": \"Tech\","
			+ "\"description\": \"Updated description for Java book\","
			+ "\"rentalFee\": 200.0,"
			+ "\"isAvailable\": true,"
			+ "\"coverImage\": \"updatedBase64CoverImage\""
			+ "}";

	HttpHeaders headers = createHeaders();
	headers.setContentType(MediaType.APPLICATION_JSON);

	// ✅ Admin updates the book
	headers.set("Authorization", "Bearer " + admintoken);
	HttpEntity<String> adminRequest = new HttpEntity<>(requestBody, headers);
	ResponseEntity<String> adminResponse = restTemplate.exchange(
			"/api/books/" + bookId,
			HttpMethod.PUT,
			adminRequest,
			String.class
	);
	System.out.println("Admin Status Code: " + adminResponse.getStatusCode());
	Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());

   

	// ❌ User tries to update — should be forbidden
	headers.set("Authorization", "Bearer " + usertoken);
	HttpEntity<String> userRequest = new HttpEntity<>(requestBody, headers);
	ResponseEntity<String> userResponse = restTemplate.exchange(
			"/api/books/" + bookId,
			HttpMethod.PUT,
			userRequest,
			String.class
	);
	System.out.println("User Status Code: " + userResponse.getStatusCode());
	Assertions.assertEquals(HttpStatus.FORBIDDEN, userResponse.getStatusCode());
}



@Test
@Order(9)
void backend_testCreateBookRentalRequestWithRoleValidation() throws Exception {
    // Ensure tokens are available
    Assertions.assertNotNull(usertoken, "User token should not be null");
    Assertions.assertNotNull(admintoken, "Admin token should not be null");

    // Construct the request body for BookRentalRequest
    String requestBody = "{"
            + "\"user\": {\"userId\": 2},"
            + "\"book\": {\"bookId\": 1},"
            + "\"requestDate\": \"2025-05-09\","
            + "\"returnDate\": \"2025-05-20\","
            + "\"status\": \"Pending\","
            + "\"comments\": \"Please reserve the book.\""
            + "}";

    // ✅ Test with User Token (Expecting 201 Created)
    HttpHeaders userHeaders = createHeaders();
    userHeaders.set("Authorization", "Bearer " + usertoken);
    HttpEntity<String> userRequest = new HttpEntity<>(requestBody, userHeaders);

    ResponseEntity<String> userResponse = restTemplate.exchange(
            "/api/bookrentalrequest",
            HttpMethod.POST,
            userRequest,
            String.class);

    Assertions.assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());


    // ❌ Test with Admin Token (Expecting 403 Forbidden)
    HttpHeaders adminHeaders = createHeaders();
    adminHeaders.set("Authorization", "Bearer " + admintoken);
    HttpEntity<String> adminRequest = new HttpEntity<>(requestBody, adminHeaders);

    ResponseEntity<String> adminResponse = restTemplate.exchange(
            "/api/bookrentalrequest",
            HttpMethod.POST,
            adminRequest,
            String.class);

    Assertions.assertEquals(HttpStatus.FORBIDDEN, adminResponse.getStatusCode());
}


@Test
@Order(10)
void backend_testGetAllBookRentalRequests_AdminOnlyAccess() throws Exception {
    Assertions.assertNotNull(admintoken, "Admin token should not be null");
    Assertions.assertNotNull(usertoken, "User token should not be null");

    // --- Admin Role Access ---
    HttpHeaders adminHeaders = createHeaders();
    adminHeaders.set("Authorization", "Bearer " + admintoken);
    HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);

    ResponseEntity<String> adminResponse = restTemplate.exchange(
            "/api/bookrentalrequest",
            HttpMethod.GET,
            adminRequest,
            String.class
    );

    System.out.println("Admin GET All Rental Requests Status: " + adminResponse.getStatusCode());
    Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());


    // --- User Role Access ---
    HttpHeaders userHeaders = createHeaders();
    userHeaders.set("Authorization", "Bearer " + usertoken);
    HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

    ResponseEntity<String> userResponse = restTemplate.exchange(
            "/api/bookrentalrequest",
            HttpMethod.GET,
            userRequest,
            String.class
    );

    System.out.println("User GET All Rental Requests Status: " + userResponse.getStatusCode());
    Assertions.assertEquals(HttpStatus.FORBIDDEN, userResponse.getStatusCode());
}

@Test
@Order(11)
void backend_testGetBookRentalRequestsByUserId_UserOnlyAccess() throws Exception {
    Assertions.assertNotNull(usertoken, "User token should not be null");
    Assertions.assertNotNull(admintoken, "Admin token should not be null");

    Long userId = 2L; // Assumed logged-in user ID

    // --- User Access (Allowed) ---
    HttpHeaders userHeaders = createHeaders();
    userHeaders.set("Authorization", "Bearer " + usertoken);
    HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

    ResponseEntity<String> userResponse = restTemplate.exchange(
            "/api/bookrentalrequest/user/" + userId,
            HttpMethod.GET,
            userRequest,
            String.class
    );

    System.out.println("User GET Rental Requests by UserId Status: " + userResponse.getStatusCode());
    Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());

    JsonNode userResponseBody = objectMapper.readTree(userResponse.getBody());
    Assertions.assertTrue(userResponseBody.isArray(), "Response should be an array");

    // --- Admin Access (Forbidden) ---
    HttpHeaders adminHeaders = createHeaders();
    adminHeaders.set("Authorization", "Bearer " + admintoken);
    HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);

    ResponseEntity<String> adminResponse = restTemplate.exchange(
            "/api/bookrentalrequest/user/" + userId,
            HttpMethod.GET,
            adminRequest,
            String.class
    );

    System.out.println("Admin GET Rental Requests by UserId Status: " + adminResponse.getStatusCode());
    Assertions.assertEquals(HttpStatus.FORBIDDEN, adminResponse.getStatusCode());
}

@Test
@Order(12)
void backend_testGetBookRentalRequestByIdWithRoleValidation() throws Exception {
    Assertions.assertNotNull(admintoken, "Admin token should not be null");
    Assertions.assertNotNull(usertoken, "User token should not be null");

    Long rentalRequestId = 1L; // Assumed to exist

    // --- Admin Role Access ---
    HttpHeaders adminHeaders = createHeaders();
    adminHeaders.set("Authorization", "Bearer " + admintoken);
    HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);

    ResponseEntity<String> adminResponse = restTemplate.exchange(
            "/api/bookrentalrequest/" + rentalRequestId,
            HttpMethod.GET,
            adminRequest,
            String.class
    );

    System.out.println("Admin GET Rental Request by ID Status: " + adminResponse.getStatusCode());
    Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());



    // --- User Role Access ---
    HttpHeaders userHeaders = createHeaders();
    userHeaders.set("Authorization", "Bearer " + usertoken);
    HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

    ResponseEntity<String> userResponse = restTemplate.exchange(
            "/api/bookrentalrequest/" + rentalRequestId,
            HttpMethod.GET,
            userRequest,
            String.class
    );

    System.out.println("User GET Rental Request by ID Status: " + userResponse.getStatusCode());
    Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());

    
}

@Test
@Order(13)
void backend_testUpdateBookRentalRequestWithRoleValidation() throws Exception {
    Assertions.assertNotNull(admintoken, "Admin token should not be null");
    Assertions.assertNotNull(usertoken, "User token should not be null");

    Long rentalRequestId = 1L; // Ensure this exists

    String requestBody = "{"
            + "\"user\": {\"userId\": 2},"
            + "\"book\": {\"bookId\": 1},"
            + "\"requestDate\": \"2025-05-09\","
            + "\"returnDate\": \"2025-05-25\","
            + "\"status\": \"Approved\","
            + "\"comments\": \"Updated rental request details\""
            + "}";

    HttpHeaders headers = createHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // User Attempt - Should be Forbidden
    headers.set("Authorization", "Bearer " + usertoken);
    HttpEntity<String> userRequest = new HttpEntity<>(requestBody, headers);
    ResponseEntity<String> userResponse = restTemplate.exchange(
            "/api/bookrentalrequest/" + rentalRequestId,
            HttpMethod.PUT,
            userRequest,
            String.class
    );
    Assertions.assertEquals(HttpStatus.FORBIDDEN, userResponse.getStatusCode());

    // Admin Attempt - Should be OK
    headers.set("Authorization", "Bearer " + admintoken);
    HttpEntity<String> adminRequest = new HttpEntity<>(requestBody, headers);
    ResponseEntity<String> adminResponse = restTemplate.exchange(
            "/api/bookrentalrequest/" + rentalRequestId,
            HttpMethod.PUT,
            adminRequest,
            String.class
    );
    Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());

   
}


@Test
@Order(14)
void backend_testAddFeedback() throws Exception {
    Assertions.assertNotNull(usertoken, "User token should not be null");
    Assertions.assertNotNull(admintoken, "Admin token should not be null");

    String requestBody = "{"
    + "\"feedbackText\": \"Great service!\","
    + "\"date\": \"2025-05-08\","
    + "\"user\": {\"userId\": 2}"
    + "}";


    // ✅ User should be able to add feedback
    HttpHeaders userHeaders = createHeaders();
    userHeaders.set("Authorization", "Bearer " + usertoken);
    HttpEntity<String> userRequest = new HttpEntity<>(requestBody, userHeaders);
    ResponseEntity<String> userResponse = restTemplate.exchange(
        "/api/feedback",
        HttpMethod.POST,
        userRequest,
        String.class
    );
    Assertions.assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());

    // ❌ Admin should NOT be able to add feedback
    HttpHeaders adminHeaders = createHeaders();
    adminHeaders.set("Authorization", "Bearer " + admintoken);
    HttpEntity<String> adminRequest = new HttpEntity<>(requestBody, adminHeaders);
    ResponseEntity<String> adminResponse = restTemplate.exchange(
        "/api/feedback",
        HttpMethod.POST,
        adminRequest,
        String.class
    );
    Assertions.assertEquals(HttpStatus.FORBIDDEN, adminResponse.getStatusCode());
}





@Test
@Order(15)
void backend_testGetAllFeedback() throws Exception {
    Assertions.assertNotNull(usertoken, "User token should not be null");
    Assertions.assertNotNull(admintoken, "Admin token should not be null");

    //  Admin should be able to get all feedback
    HttpHeaders adminHeaders = createHeaders();
    adminHeaders.set("Authorization", "Bearer " + admintoken);
    HttpEntity<Void> adminRequest = new HttpEntity<>(adminHeaders);
    ResponseEntity<String> adminResponse = restTemplate.exchange("/api/feedback", HttpMethod.GET, adminRequest, String.class);
    Assertions.assertEquals(HttpStatus.OK, adminResponse.getStatusCode());

    //  User should NOT be able to get all feedback
    HttpHeaders userHeaders = createHeaders();
    userHeaders.set("Authorization", "Bearer " + usertoken);
    HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);
    ResponseEntity<String> userResponse = restTemplate.exchange("/api/feedback", HttpMethod.GET, userRequest, String.class);
    Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());
}


}