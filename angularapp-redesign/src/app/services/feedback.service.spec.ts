import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FeedbackService } from './feedback.service';
import { Feedback } from '../models/feedback.model';

describe('FeedbackService', () => {
  let service: FeedbackService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FeedbackService]
    });
    service = TestBed.inject(FeedbackService);
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

  it('should send feedback', () => {
    const dummy: Feedback = {
      userId: 2,
      feedbackText: 'Great platform',
      date: '2025-05-08'
    };

    service.sendFeedback(dummy).subscribe((res) => {
      expect(res.feedbackId).toBe(1);
    });

    const req = httpMock.expectOne(`${service.apiUrl}/api/feedback`);
    expect(req.request.method).toBe('POST');
    req.flush({ ...dummy, feedbackId: 1 });
  });

  it('should get all feedbacks', () => {
    const dummyList: Feedback[] = [
      {
        feedbackId: 1,
        userId: 2,
        feedbackText: 'Great platform',
        date: '2025-05-08'
      }
    ];

    service.getFeedbacks().subscribe((res) => {
      expect(res.length).toBe(1);
      expect(res[0].feedbackText).toBe('Great platform');
    });

    const req = httpMock.expectOne(`${service.apiUrl}/api/feedback`);
    expect(req.request.method).toBe('GET');
    req.flush(dummyList);
  });
});
