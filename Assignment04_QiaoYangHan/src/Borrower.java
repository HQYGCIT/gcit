import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Borrower extends BaseDAO {
	private final int cardNo;
	
	public Borrower() {
		this.cardNo = readCardNo();
	}
	
	private Integer readCardNo() {
		String selectCardNo = "select cardNo from tbl_borrower";
		ResultSet rs = readAll(selectCardNo, new Object[0]);
		List<Integer> cardNos = getCardNos(rs);
		
		System.out.println("Enter the your Card Number:");
		int cardNo = IOTool.readInteger();
		while(!cardNos.contains(cardNo)) {
			System.out.println("This Card Number is invalid. Please enter a valid Card Number:");
			cardNo = IOTool.readInteger();
		}
		return cardNo;
	}
	
	private LibraryBranch chooseBranch() {
		System.out.println("Choose a library branch:");
		String selectBranchQuery = "select branchId, branchName, branchAddress from tbl_library_branch order by branchId";
		ResultSet rs = readAll(selectBranchQuery, new Object[0]);
		List<LibraryBranch> libraryBranches = getLibraryBranches(rs);
		printList(libraryBranches);
		int choice = IOTool.readInteger(1, libraryBranches.size()+1);
		return libraryBranches.get(choice-1);
	}
	
	public void borrowerMenu() {
		String head = String.format("Borrower Mode (Card Number %d)%n", cardNo);
		List<String> content = new ArrayList<>();
		content.add("Check out a book");
		content.add("Return a book");
		content.add("Quit to previous");
		IOTool.displayMenu(head, content);
		
		Integer choice = IOTool.readInteger(1, content.size()+1);
		switch(choice) {
			case 1:
				break;
			case 2:
				break;
			default:
		}
	}
	
	public void checkOutBook() {
		LibraryBranch libraryBranch = chooseBranch();
		//select the books that are available and haven't been checked out by the borrower
		String selectAvailableBooks = "select * from tbl_book b inner join tbl_book_authors ba on ba.bookId = b.bookId inner join tbl_author a on a.authorId = ba.authorId "
				+ "inner join tbl_book_copies bc on bc.bookId = b.bookId "
				+ "where bc.branchId = ? and bc.noOfCopies > 0 and b.bookId not in "
				+ "(select bookId from tbl_book_loans where cardNo = ? and branchId = ?)";
		Object[] values = new Object[3];
		values[0] = libraryBranch.getBranchId();
		values[1] = cardNo;
		values[2] = libraryBranch.getBranchId();
		ResultSet rs = readAll(selectAvailableBooks, values);
		List<Book> availableBooks = getBooks(rs);
		
		System.out.println("Choose a book:");
		printList(availableBooks);
		int choice = IOTool.readInteger(1, availableBooks.size()+1);
		Book chosenBook = availableBooks.get(choice-1);
		
		String insertChosenBook = "insert into tbl_book_loans (bookId, branchId, cardNo, dateOut, dueDate) values (?, ?, ?, ?, ?)";
		values = new Object[5];
		values[0] = chosenBook.getBookId();
		values[1] = libraryBranch.getBranchId();
		values[2] = cardNo;
		Calendar now = Calendar.getInstance();
		Calendar nextWeek = now;
		nextWeek.add(Calendar.DATE, 7);
		values[3] = new Timestamp(now.getTimeInMillis());
		values[4] = new Timestamp(nextWeek.getTimeInMillis());
		save(insertChosenBook, values);
		System.out.printf("You have successfully checked out %s from %s at %s", chosenBook, libraryBranch, now);
	}
	
	public void returnBook(int branchId) {
		String checkedOutBooksQuery = "select b.bookId, b.title, a.authorName from tbl_author a inner join tbl_book_authors ba on a.authorId = ba.authorId inner join tbl_book b on ba.bookId = b.bookId inner join tbl_book_loans bl on b.bookId = bl.bookId where bl.cardNo = ? and bl.branchId = ?";
		Object[] values = new Object[] {cardNo, branchId};
		List<Book> checkedOutBooks = (List<Book>) readAll(checkedOutBooksQuery, values);
		printList(checkedOutBooks);
	}
	
	public List<Integer> getCardNos(ResultSet rs) {
		List<Integer> cardNos = new ArrayList<>();
		try {
			while(rs.next()) {
				int cardNo = rs.getInt("cardNo");
				cardNos.add(cardNo);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when getting card numbers.");
			System.exit(0);
		}
		return cardNos;
	}
	
	public List<Book> getBooks(ResultSet rs) {
		List<Book> books = new ArrayList<>();
		try {
			while(rs.next()) {
				int bookId = rs.getInt("bookId");
				String title = rs.getString("title");
				String authorName = rs.getString("authorName");
				books.add(new Book(bookId, title, authorName));
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when getting books.");
			System.exit(0);
		}
		return books;
	}
	
	public List<LibraryBranch> getLibraryBranches(ResultSet rs) {
		List<LibraryBranch> libraryBranches = new ArrayList<>();
		try {
			while(rs.next()) {
				int branchId = rs.getInt("branchId");
				String branchName = rs.getString("branchName");
				String branchAddress = rs.getString("branchAddress");
				libraryBranches.add(new LibraryBranch(branchId, branchName, branchAddress));
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when getting library branches.");
			System.exit(0);
		}
		return libraryBranches;
	}
}