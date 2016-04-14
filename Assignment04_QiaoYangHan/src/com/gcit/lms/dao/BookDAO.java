package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.Publisher;

public class BookDAO extends BaseDAO {
	public BookDAO(Connection conn) {
		super(conn);
	}

	public void addBook(Book book) {
		save("insert into tbl_book (title, pubId) values (?, ?)", new Object[] {book.getTitle(), book.getPublisher().getPublisherId()});
	}

	public void addBookGetId(Book book) {
		saveGetId("insert into tbl_book (title, pubId) values (?, ?)", new Object[] {book.getTitle(), book.getPublisher().getPublisherId()});
	}
	
	public void updateBook(Book book) {
		save("update tbl_book set title = ? and pubId = ? where bookId = ?", new Object[] {book.getTitle(), book.getPublisher().getPublisherId(), book.getBookId()});
	}

	public void deleteBook(Book book) {
		save("delete from tbl_book where bookId = ?", new Object[] {book.getBookId()});
	}

	public List<Book> readAllBooks() {
		return (List<Book>) readAll("select * from tbl_book", new Object[0]);
	}
	
	public List<Book> readBooksByTitle(String title) {
		return (List<Book>) readAll("select * from tbl_book where title like ?", new Object[]{title});
	}

	public Integer getCount() {
		return getCount("select count(*)");
	}
	
	@Override
	protected List<Book> extractData(ResultSet rs) {
		List<Book> books = new ArrayList<>();
		AuthorDAO aDao = new AuthorDAO(getConnection());
		GenreDAO gDao = new GenreDAO(getConnection());
		PublisherDAO pDao = new PublisherDAO(getConnection());
		try {
			while(rs.next()) {
				Book b = new Book();
				b.setBookId(rs.getInt("BookId"));
				List<Author> authors = (List<Author>) aDao.readFirstLevel("select * from tbl_author where authorId in (select authorId from tbl_book_authors where bookId = ?)", new Object[] {b.getBookId()});
				List<Genre> genres = (List<Genre>) gDao.readFirstLevel("select * from tbl_genre where genre_id in (select genre_id from tbl_book_genres where bookId = ?)", new Object[]{b.getBookId()});
				//needs work
				List<Publisher> publishers = (List<Publisher>) pDao.readFirstLevel("select * from tbl_publisher where publisherId in (select pubId from tbl_book where bookId = ?)", new Object[]{b.getBookId()});
				b.setAuthors(authors);
				b.setGenres(genres);
				b.setPublisher(publishers.get(0));
				books.add(b);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when extracting data");
			System.exit(0);
		}
		return books;
	}

	@Override
	public List<Book> extractDataFirstLevel(ResultSet rs) {
		List<Book> books = new ArrayList<>();
		try {
			while(rs.next()) {
				Book b = new Book();
				b.setBookId(rs.getInt("bookId"));
				b.setTitle(rs.getString("title"));
				books.add(b);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when extracting data first level");
			System.exit(0);
		}
		return books;
	}
}