import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Timestamp;

import java.util.ArrayList;

public class Administrator {
	private final LibraryManagementSystem lms;
	
	public Administrator(LibraryManagementSystem lms) {
		this.lms = lms;
	}
	
	public void administratorMenu() {
		List<String> content = new ArrayList<>();
		content.add("Book and Author");
		content.add("Publishers");
		content.add("Library Branches");
		content.add("Borrowers");
		content.add("Quit to previous");
		
		IOTool.displayMenu("Administrator Mode\n", content);
		
		Integer choice = IOTool.readInteger(1, content.size()+1);
		switch(choice) {
			case 1:
				administratorMenu2("Book and Author");
				break;
			case 2:
				administratorMenu2("Publishers");
				break;
			case 3:
				administratorMenu2("Library Branches");
				break;
			case 4:
				administratorMenu2("Borrowers");
				break;
			default:
				lms.mainMenu();
		}
	}
	
	private void administratorMenu2(String table) {
		List<String> content = new ArrayList<>();
		content.add("Add");
		content.add("Update");
		content.add("Delete");
		content.add("Quit to previous");
		
		IOTool.displayMenu(String.format("Administrator Mode%n%n%s", table), content);
		
		Integer choice = IOTool.readDigit(System.in, 1, content.size());
		
		switch(choice) {
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			default:
				administratorMenu();
		}
	}
	
	//add new book with new title and retrieve this new book's bookId
	private Integer addBook(String title) {
		Connection conn = lms.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Integer bookId = null;
		try {
			String insertBook = "insert into tbl_book (title) values (?)";
			pstmt = conn.prepareStatement(insertBook);
			pstmt.setString(1, title);
			pstmt.executeUpdate();
			
			String selectBookId = "select bookId from tbl_book where title = ? order by bookId desc";
			pstmt = conn.prepareStatement(selectBookId);
			pstmt.setString(1, title);
			rs = pstmt.executeQuery();
			
			bookId = rs.next() ? rs.getInt("bookId") : null;
			return bookId;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bookId;
	}
	
	//add new author with new author name and retrieve this new author's authorId
	private Integer addAuthor(String authorName) {
		Connection conn = lms.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Integer authorId = null;
		try {
			String insertAuthor = "insert into tbl_author (authorName) values (?)";
			pstmt = conn.prepareStatement(insertAuthor);
			pstmt.setString(1, authorName);
			pstmt.executeUpdate();
			
			String selectAuthorId = "select authorId from tbl_author where authorName = ? order by authorId desc";
			pstmt = conn.prepareStatement(selectAuthorId);
			pstmt.setString(1, authorName);
			rs = pstmt.executeQuery();
			
			authorId = rs.next() ? rs.getInt("authorId") : null;
			return authorId;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return authorId;
	}
	
	//retrieve a list of authors for the administrator to choose which one is the new book's author
	public List<Author> getAuthors() {
		Connection conn = lms.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Author> authors = null;
		try {
			String selectAuthors = "select authorId, authorName from tbl_author";
			pstmt = conn.prepareStatement(selectAuthors);
			rs = pstmt.executeQuery();
			
			authors = new ArrayList<>();
			while(rs.next()) {
				int authorId = rs.getInt("authorId");
				String authorName = rs.getString("authorName");
				authors.add(new Author(authorId, authorName));
			}
			return authors;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return authors;
	}
	
	public List<Publisher> getPublishers() {
		Connection conn = lms.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Publisher> publishers = null;
		try {
			String selectPublishers = "select publisherId, publisherName, publisherAddress, publisherPhone from tbl_publisher";
			pstmt = conn.prepareStatement(selectPublishers);
			rs = pstmt.executeQuery();
			
			publishers = new ArrayList<>();
			while(rs.next()) {
				int publisherId = rs.getInt("publisherId");
				String publisherName = rs.getString("publisherName");
				String publisherAddress = rs.getString("publisherAddress");
				String publisherPhone = rs.getString("publisherPhone");
				publishers.add(new Publisher(publisherId, publisherName, publisherAddress, publisherPhone));
			}
			return publishers;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return publishers;
	}

	public List<Genre> getGenre() {
		Connection conn = lms.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Genre> genres = null;
		try {
			String selectGenres = "select genre_id, genre_name from tbl_genre";
			pstmt = conn.prepareStatement(selectGenres);
			rs = pstmt.executeQuery();
			
			genres = new ArrayList<>();
			while(rs.next()) {
				int genreId = rs.getInt("genre_id");
				String genreName = rs.getString("genre_name");
				genres.add(new Genre(genreId, genreName));
			}
			return genres;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return genres;
	}

	public <T> void printList(List<T> l) {
		for(int i=0; i<l.size(); i+=1) {
			System.out.printf("d) %s%n", i+1, l.get(i));
		}
	}

	public void setPreparedStatement(PreparedStatement pstmt, List<Object> values) {
		try {
			for(int i=0; i<values.size(); i+=1) {
				Object o = values.get(i);
				if(o.getClass() == Integer.class) {
					pstmt.setInt(i+1, (Integer) o);
				}
				else if(o.getClass() == String.class) {
					pstmt.setString(i+1, (String) o);
				}
				else if(o.getClass() == Timestamp.class) {
					pstmt.setTimestamp(i+1, (Timestamp) o);
				}
				else {
					//default
				}
			}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Integer addEntry(String pkName, String insertQuery, List<Object> insertValues, String selectQuery, List<Object> selectValues) {
		Connection conn = lms.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Integer pk = null;
		try {
			pstmt = conn.prepareStatement(insertQuery);
			setPreparedStatement(pstmt, insertValues);
			pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement(selectQuery);
			setPreparedStatement(pstmt, selectValues);
			rs = pstmt.executeQuery();
			
			pk = rs.next() ? rs.getInt(pkName) : null;
			return pk;
		} catch (SQLException e) {
			System.err.println("SQL Exception when adding an Entry.");
			System.exit(0);
		}
		return pk;
	}
	
	public static void main(String[] args) {
		
	}
}