package fr.d2factory.libraryapp.book;

/**
 * A simple representation of a book
 */
public class Book {
    String title;
    String author;
    ISBN isbn;
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setIsbn(ISBN isbn) {
		this.isbn = isbn;
	}

	

    public Book(String title, String author, ISBN isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
}
