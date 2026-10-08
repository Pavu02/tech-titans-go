import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BookrentalrequestService } from './bookrentalrequest.service';
import { BookRentalRequest } from '../models/book-rental-request.model';

describe('BookrentalrequestService', () => {
  let service: BookrentalrequestService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BookrentalrequestService]
    });
    service = TestBed.inject(BookrentalrequestService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.setItem('token', 'mock-token');
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all rental requests', () => {
    const dummy: BookRentalRequest[] = [
      {
        rentalId: 1,
        userId: 2,
        bookId: 1,
        requestDate: '2025-05-09',
        returnDate: '2025-05-20',
        status: 'Pending',
        comments: 'Reserve'
      }
    ];

    service.getAllBookRentalRequests().subscribe((data) => {
      expect(data.length).toBe(1);
      expect(data[0].status).toBe('Pending');
    });

    const req = httpMock.expectOne(`${service.apiUrl}/api/bookrentalrequest`);
    expect(req.request.method).toBe('GET');
    req.flush(dummy);
  });

  it('should add a rental request', () => {
    const dummy: BookRentalRequest = {
      userId: 2,
      bookId: 1,
      requestDate: '2025-05-09',
      returnDate: '2025-05-20',
      status: 'Pending',
      comments: 'Reserve'
    };

    service.addBookRentalRequest(dummy).subscribe((res) => {
      expect(res.rentalId).toBe(1);
    });

    const req = httpMock.expectOne(`${service.apiUrl}/api/bookrentalrequest`);
    expect(req.request.method).toBe('POST');
    req.flush({ ...dummy, rentalId: 1 });
  });
});
