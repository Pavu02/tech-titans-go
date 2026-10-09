import { Book } from './book.model';
import { User } from './user.model';

export interface BookRentalRequest {
  rentalId?: number;
  userId?: number;
  bookId?: number;
  user?: User;
  book?: Book;
  requestDate: string;
  returnDate: string;
  status: string; // 'Pending' | 'Approved' | 'Returned' | 'Rejected'
  comments: string;
  // UI convenience properties
  username?: string;
  bookTitle?: string;
  author?: string;
  genre?: string;
  rentalFee?: number;
  coverImage?: string;
  description?: string;
  totalRentalAmount?: number;
}
