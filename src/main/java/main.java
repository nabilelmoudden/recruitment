
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.library.BookLibrary;

public class main {
	
	public static void setup(){
		BookRepository bookRepository = new BookRepository();
		BookLibrary library = new BookLibrary(bookRepository);
		// Reading books from json file
		List<Book> list = new ArrayList<>();
		try {
			JSONParser parser = new JSONParser();
			JSONArray a = (JSONArray) parser.parse(new FileReader("src\\test\\resources\\books.json"));
			
			for (Object o : a) {
				JSONObject book = (JSONObject) o;
				String title = (String) book.get("title");
				String author = (String) book.get("author");
				long isbn = (long) ((JSONObject) book.get("isbn")).get("isbnCode");
				Book tmp = new Book(title, author, new ISBN(isbn));
				list.add(tmp);
				 
			}
			 
		} catch (ParseException | IOException e) {
		 
		} 
		bookRepository.addBooks(list);
		for(Book book:list) {
		System.out.println(book.getTitle());
		}
		for(Book book:list) {
			
			System.out.println(book.getIsbn().getIsbnCode());
		}
	}

	public static void main(String[] args) {
		BookRepository bookRepository = new BookRepository();
		BookLibrary library = new BookLibrary(bookRepository);
		System.out.println("__________init______");
		setup();
		
	
	}

}
