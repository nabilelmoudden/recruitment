package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {
    private Map<ISBN, Book> availableBooks = new HashMap<>();
    private Map<Book, LocalDate> borrowedBooks = new HashMap<>();

    public void addBooks(List<Book> books){
    	 if (books != null && !books.isEmpty()) {

             books.stream().forEach(book -> {
                 if (book != null) {
                     availableBooks.put(book.getIsbn(), book);
                 }
             });
         }
    }

    public Book findBook(long isbnCode) {
    	 return this.availableBooks.entrySet().stream().filter(v -> v.getKey().getIsbnCode() ==
                 isbnCode).map(Map.Entry::getValue)
                 .findFirst()
                 .orElse(null);

    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt){
    	borrowedBooks.put(book, borrowedAt);
    }

    public LocalDate findBorrowedBookDate(Book book) {
    	if(book!= null) {
    		 return this.borrowedBooks.entrySet().stream().filter(b -> b.getKey().equals(book)).map(Map.Entry::getValue)
    	                .findFirst()
    	                .orElse(null);

		}
		return null;
    }
    
	public Book removeBook(long isbnCode) {		
		Iterator<Map.Entry<ISBN, Book>> iterator = availableBooks.entrySet().iterator();
		 while (iterator.hasNext()) {
			Map.Entry<ISBN, Book> entry = iterator.next();
			if (entry.getKey().getIsbnCode() == isbnCode ) {
				availableBooks.remove(entry.getKey());
				return entry.getValue();				
			}
		}
		return null;		
	}

	public Map<ISBN, Book> getAvailableBooks() {
		return availableBooks;
	}

	public void setAvailableBooks(Map<ISBN, Book> availableBooks) {
		this.availableBooks = availableBooks;
	}

	public Map<Book, LocalDate> getBorrowedBooks() {
		return borrowedBooks;
	}

	public void setBorrowedBooks(Map<Book, LocalDate> borrowedBooks) {
		this.borrowedBooks = borrowedBooks;
	}
}
