import { Book } from './book.model';

describe('Book Model', () => {
  it('should create a valid book object', () => {
    const book: Book = {
      bookId: 1,
      title: 'Effective Java',
      author: 'Joshua Bloch',
      genre: 'Programming',
      description: 'A best-practices guide for Java developers',
      rentalFee: 150,
      isAvailable: true,
      coverImage: 'cover.jpg'
    };
    expect(book).toBeTruthy();
    expect(book.title).toBe('Effective Java');
    expect(book.isAvailable).toBeTrue();
  });
});
