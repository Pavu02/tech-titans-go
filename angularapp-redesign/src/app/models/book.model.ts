export interface Book {
  bookId?: number;
  title: string;
  author: string;
  genre: string;
  description: string;
  rentalFee: number;
  isAvailable: boolean;
  coverImage: string;
}
