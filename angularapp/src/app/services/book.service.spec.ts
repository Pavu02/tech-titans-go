import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BookService } from './book.service';
import { Book } from '../models/book.model';

describe('BookService', () => {
  let service: BookService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BookService]
    });
    service = TestBed.inject(BookService);
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

  it('should retrieve all books', () => {
    const dummyBooks: Book[] = [
      {
        bookId: 1,
        title: 'Effective Java',
        author: 'Joshua Bloch',
        genre: 'Tech',
        description: 'Guide',
        rentalFee: 150,
        isAvailable: true,
        coverImage: 'img'
      }
    ];

    service.getAllBooks().subscribe((books) => {
      expect(books.length).toBe(1);
      expect(books).toEqual(dummyBooks);
    });

    const req = httpMock.expectOne(`${service.apiUrl}/api/books`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    req.flush(dummyBooks);
  });

  it('should get book by id', () => {
    const dummyBook: Book = {
      bookId: 1,
      title: 'Effective Java',
      author: 'Joshua Bloch',
      genre: 'Tech',
      description: 'Guide',
      rentalFee: 150,
      isAvailable: true,
      coverImage: 'img'
    };

    service.getBookById(1).subscribe((book) => {
      expect(book).toEqual(dummyBook);
    });

    const req = httpMock.expectOne(`${service.apiUrl}/api/books/1`);
    expect(req.request.method).toBe('GET');
    req.flush(dummyBook);
  });

  it('should add a book', () => {
    const newBook: Book = {
      title: 'Clean Code',
      author: 'Robert Martin',
      genre: 'Tech',
      description: 'Clean coding',
      rentalFee: 200,
      isAvailable: true,
      coverImage: 'img'
    };

    service.addBook(newBook).subscribe((b) => {
      expect(b.title).toBe('Clean Code');
    });

    const req = httpMock.expectOne(`${service.apiUrl}/api/books`);
    expect(req.request.method).toBe('POST');
    req.flush({ ...newBook, bookId: 2 });
  });

  it('should delete a book', () => {
    service.deleteBook(1).subscribe((res) => {
      expect(res).toBeFalsy();
    });

    const req = httpMock.expectOne(`${service.apiUrl}/api/books/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
