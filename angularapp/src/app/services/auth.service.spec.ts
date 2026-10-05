import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { User } from '../models/user.model';
import { Login } from '../models/login.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should register user via POST', () => {
    const dummyUser: User = {
      username: 'test',
      email: 'test@example.com',
      password: 'password',
      mobileNumber: '1234567890',
      userRole: 'User'
    };

    service.register(dummyUser).subscribe((res) => {
      expect(res).toEqual(dummyUser);
    });

    const req = httpMock.expectOne(`${service.apiUrl}/api/register`);
    expect(req.request.method).toBe('POST');
    req.flush(dummyUser);
  });

  it('should login user and store token', () => {
    const dummyLogin: Login = { email: 'test@example.com', password: 'password' };
    const dummyResponse = {
      token: 'mock-jwt-token',
      userId: 1,
      email: 'test@example.com',
      username: 'test',
      userRole: 'User'
    };

    service.login(dummyLogin).subscribe((res) => {
      expect(res.token).toBe('mock-jwt-token');
      expect(service.getToken()).toBe('mock-jwt-token');
      expect(service.getUserRole()).toBe('User');
    });

    const req = httpMock.expectOne(`${service.apiUrl}/api/login`);
    expect(req.request.method).toBe('POST');
    req.flush(dummyResponse);
  });

  it('should clear storage on logout', () => {
    localStorage.setItem('token', 'sample');
    service.logout();
    expect(service.getToken()).toBeNull();
  });
});
