package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.reflect.Field;
import java.util.HashMap;

public class LibraryTest {
	private Library library ;
	private BookRepository bookRepository;
	private List<Book> listOfBooks;
	private static final int daysBeforeLateStudent = 30;
	private static final int daysBeforeLateResident = 60;
	private static final int ResidentPriceBeforeLate = 10;
	private static final int ResidentPriceafterLate  = 20;
	private static final int StudentPriceafterLate  = 15;
	private static final int StudentPricebeforeLate  = 10;
	private static final int firstYearFree = 15;
	@Before
	public void setup(){
		//TODO instantiate the library and the repository
		// new instances for each test
		bookRepository = new BookRepository();
		library = new BookLibrary(bookRepository);

		// Reading books from json file
		try {
			JSONParser parser = new JSONParser();
			JSONArray a = (JSONArray) parser.parse(new FileReader("src\\test\\resources\\books.json"));
			List<Book> list = new ArrayList<>();
			for (Object o : a) {
				JSONObject book = (JSONObject) o;
				String title = (String) book.get("title");
				String author = (String) book.get("author");
				long isbn = (long) ((JSONObject) book.get("isbn")).get("isbnCode");
				Book tmp = new Book(title, author, new ISBN(isbn));
				list.add(tmp);
			}
			listOfBooks = Collections.unmodifiableList(list);
		} catch (ParseException | IOException e) {
			fail("file books.json not found or error while parsing json");
		}
		// filling library with books

		//TODO add some test books (use BookRepository#addBooks)
		//TODO to help you a file called books.json is available in src/test/resources
		bookRepository.addBooks(listOfBooks);
	}

	@Test
	public void member_can_borrow_a_book_if_book_is_available(){
		try {
			// Temporary object
			Member alexander = new Student(true,1000000);

			// Tested method
			library.borrowBook(46578964513L, alexander, LocalDate.now());

			// Reflection on private fields
			Field f = bookRepository.getClass().getDeclaredField("availableBooks"); //NoSuchFieldException
			f.setAccessible(true);
			HashMap availableBooks = (HashMap) f.get(bookRepository); //IllegalAccessException
			f = bookRepository.getClass().getDeclaredField("borrowedBooks"); //NoSuchFieldException
			f.setAccessible(true);
			HashMap borrowedBooks = (HashMap) f.get(bookRepository);

			//testing if a book moved from available to borrowedBooks
			Assert.assertEquals("available books list wasn't decremented by 1",3, availableBooks.size());
			Assert.assertEquals("borrowed books list wasn't incremented by 1",1, borrowedBooks.size());
		} catch (BookNotFoundException e) {
			fail("book not found in library ");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			fail("test failed during reflextion : " + e.getMessage() );
		}
	}

	@Test
	public void borrowed_book_is_no_longer_available(){
		Member mathieu = new Student(true,1000000);
		// unknown ISBN
		library.borrowBook(000000000L, mathieu, LocalDate.now());
	}

	@Test
	public void residents_are_taxed_10cents_for_each_day_they_keep_a_book(){
		Member mathieu = new Resident(1000);

		// 10 days *  10 cents  = 100 cents
		library.borrowBook(listOfBooks.get(0).getIsbn().getIsbnCode(), mathieu, LocalDate.now().minusDays(10));
		library.returnBook(listOfBooks.get(0), mathieu);
		Assert.assertEquals( 900 , mathieu.getWallet(),0);
	}

	@Test
	public void students_pay_10_cents_the_first_30days(){
		Member mathieu = new Resident(1000);

		// 30 days *  10 cents  = 300 cents
		library.borrowBook(listOfBooks.get(0).getIsbn().getIsbnCode(), mathieu, LocalDate.now().minusDays(30));
		library.returnBook(listOfBooks.get(0), mathieu);
		Assert.assertEquals( 700 , mathieu.getWallet(),0);
	}

	@Test
	public void students_in_1st_year_are_not_taxed_for_the_first_15days(){
		int wallet = 100;
		Member mathieu = new Student(true,wallet);

		// get student's free trial's number of days from config
		library.borrowBook(listOfBooks.get(0).getIsbn().getIsbnCode(), mathieu, LocalDate.now().minusDays(firstYearFree));
		library.returnBook(listOfBooks.get(0), mathieu);
		Assert.assertEquals( wallet , mathieu.getWallet(),0);
	}

	@Test
	public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days(){
		Member mathieu = new Student(false,1000);
		int days = daysBeforeLateStudent  + 10;

		library.borrowBook(listOfBooks.get(0).getIsbn().getIsbnCode(), mathieu, LocalDate.now().minusDays(days));
		library.returnBook(listOfBooks.get(0), mathieu);

		// (days before late* price before being late) + (rest of days * price after being late))
		int price =daysBeforeLateStudent *StudentPricebeforeLate+(days - daysBeforeLateStudent)*(StudentPriceafterLate);
		Assert.assertEquals( 1000 - price, mathieu.getWallet(), 0);
	}

	@Test
	public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days(){
		Member mathieu = new Resident(2000);
		int days = daysBeforeLateResident + 10;

		library.borrowBook(listOfBooks.get(0).getIsbn().getIsbnCode(), mathieu, LocalDate.now().minusDays(days));
		library.returnBook(listOfBooks.get(0), mathieu);

		// (days before late* price before being late) + (rest of days * price after being late))
		int price = (daysBeforeLateResident *ResidentPriceBeforeLate ) + (days - daysBeforeLateResident)*ResidentPriceafterLate;
		Assert.assertEquals( 2000 - price, mathieu.getWallet(),0);
	}

	@Test
	public void members_cannot_borrow_book_if_they_have_late_books(){
		Member mathieu = new Resident(2000);
		library.borrowBook(listOfBooks.get(0).getIsbn().getIsbnCode(), mathieu, LocalDate.now().minusDays(daysBeforeLateResident + 10));
		library.borrowBook(listOfBooks.get(1).getIsbn().getIsbnCode(), mathieu, LocalDate.now());
	}
}
