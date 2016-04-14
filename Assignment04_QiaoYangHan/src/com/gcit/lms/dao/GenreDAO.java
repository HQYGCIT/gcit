package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;

public class GenreDAO extends BaseDAO {
	public GenreDAO(Connection conn) {
		super(conn);
	}

	public void addGenre(Genre genre) {
		save("insert into tbl_genre (genre_name) values (?)", new Object[] {genre.getGenreName()});
	}

	public void addGenreGetId(Genre genre) {
		saveGetId("insert into tbl_genre (genre_name) values (?)", new Object[] {genre.getGenreName()});
	}
	
	public void updateGenre(Genre genre) {
		save("update tbl_genre set genre_name = ? where genre_id = ?", new Object[] {genre.getGenreName(), genre.getGenreId()});
	}

	public void deleteGenre(Genre genre) {
		save("delete from tbl_genre where genre_id = ?", new Object[] {genre.getGenreId()});
	}

	public List<Genre> readAllGenres() {
		return (List<Genre>) readAll("select * from tbl_genre", new Object[0]);
	}
	
	public List<Genre> readGenresByName(String genreName) {
		return (List<Genre>) readAll("select * from tbl_genre where genre_name like ?", new Object[]{genreName});
	}

	public Integer getCount() {
		return getCount("select count(*) from tbl_genre");
	}

	@Override
	protected List<Genre> extractData(ResultSet rs) {
		List<Genre> genres = new ArrayList<>();
		BookDAO bDao = new BookDAO(getConnection());
		try {
			while(rs.next()) {
				Genre g = new Genre();
				g.setGenreId(rs.getInt("genre_id"));
				g.setGenreName(rs.getString("genre_name"));
				List<Book> books = (List<Book>) bDao.readFirstLevel("select * from tbl_book where bookId in (select bookId from tbl_book_genres where genre_id = ?)", new Object[]{g.getGenreId()});
				g.setBooks(books);
				genres.add(g);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when extracting data");
			System.exit(0);
		}
		return genres;
	}

	@Override
	protected List<Genre> extractDataFirstLevel(ResultSet rs) {
		List<Genre> genres = new ArrayList<>();
		try {
			while(rs.next()) {
				Genre g = new Genre();
				g.setGenreId(rs.getInt("genre_id"));
				g.setGenreName(rs.getString("genre_name"));
				genres.add(g);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when extracting data first level");
			System.exit(0);
		}
		return genres;
	}
}