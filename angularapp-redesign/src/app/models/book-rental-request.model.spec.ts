import { BookRentalRequest } from './book-rental-request.model';

describe('BookRentalRequest Model', () => {
  it('should create a valid rental request object', () => {
    const request: BookRentalRequest = {
      rentalId: 1,
      userId: 2,
      bookId: 1,
      requestDate: '2025-05-09',
      returnDate: '2025-05-20',
      status: 'Pending',
      comments: 'Reserve please'
    };
    expect(request).toBeTruthy();
    expect(request.status).toBe('Pending');
  });
});
