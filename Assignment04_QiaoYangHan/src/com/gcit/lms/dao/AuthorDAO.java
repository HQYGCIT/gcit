package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;

public class AuthorDAO extends BaseDAO {
	public AuthorDAO(Connection conn) {
		super(conn);
	}

	public void addAuthor(Author author) {
		save("insert into tbl_author (authorName) values (?)", new Object[] {author.getAuthorName()});
	}
	
	public Integer addAuthorGetId(Author author) {
		return saveGetId("insert into tbl_author (authorName) values (?)", new Object[] {author.getAuthorName()});
	}
	
	public void updateAuthor(Author author) {
		save("update tbl_author set authorName = ? where authorId = ?", new Object[] {author.getAuthorName(), author.getAuthorId()});
	}
	
	public void deleteAuthor(Author author) {
		save("delete from tbl_author where authorId = ?", new Object[] {author.getAuthorId()});
	}
	
	public List<Author> readAllAuthors() {
		return (List<Author>) readAll("select * from tbl_author", new Object[0]);
	}
	
	public List<Author> readAuthorsByName(String name) {
		return (List<Author>) readAll("select * from tbl_author where authorName like ?", new Object[] {name});
	}
	
	public Integer getCount() {
		return getCount("select count(*) from tbl_author");
	}
	
	@Override
	public List<Author> extractData(ResultSet rs) {
		List<Author> authors = new ArrayList<Author>();
		BookDAO bDao = new BookDAO(getConnection());
		try {
			while(rs.next()) {
				Author a = new Author();
				a.setAuthorId(rs.getInt("authorId"));
				a.setAuthorName(rs.getString("authorName"));
				List<Book> books = (List<Book>) bDao.readFirstLevel("select * from tbl_book where bookId in (select bookId from tbl_book_authors where authorId = ?)", new Object[] {a.getAuthorId()});
				a.setBooks(books);
				authors.add(a);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when extracting data.");
			System.exit(0);
		}
		return authors;
	}
	
	@Override
	public List<Author> extractDataFirstLevel(ResultSet rs) {
		List<Author> authors = new ArrayList<Author>();
		try {
			while(rs.next()){
				Author a = new Author();
				a.setAuthorId(rs.getInt("authorId"));
				a.setAuthorName(rs.getString("authorName"));
				authors.add(a);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when extracting data first level.");
			System.exit(0);
		}
		return authors;
	}
}