import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Librarian {
	
	private final LibraryManagementSystem lms;
	
	public Librarian(LibraryManagementSystem lms) {
		this.lms = lms;
	}
	
	public void librarianMenu() {
		List<String> content = new ArrayList<>();
		content.add("Enter Branch you manage");
		content.add("Quit to previous");
		
		IOTool.displayMenu("Librarian Mode\n", content);
		
		Integer choice = IOTool.readDigit(System.in, 1, content.size());
		switch(choice) {
			case 1:
				librarianMenu2();
				break;
			default:
				lms.mainMenu();
		}
	}
	
	private void librarianMenu2() {
		PreparedStatement pstmt = null;
		String selectQuery = "select branchName, branchAddress from tbl_library_branch order by branchId";
		try {
			pstmt = lms.getConnection().prepareStatement(selectQuery);
			ResultSet rs = pstmt.executeQuery();
			List<String> content = new ArrayList<>();
			while(rs.next()) {
				String ss = String.format("%s, %s", rs.getString("branchName"), rs.getString("branchAddress"));
				content.add(ss);
			}
			pstmt.close();
			content.add("Quit to previous");
			IOTool.displayMenu("Librarian Mode\n", content);
			
			Integer choice = IOTool.readDigit(System.in, 1, content.size());
			if(choice == content.size()) {
				librarianMenu();
			}
			else {
				librarianMenu3(choice, content.get(choice-1).split(", ")[0]);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when selecting library branches");
			System.exit(0);
		}
	}
	
	private void librarianMenu3(int branchId, String branchName) {
		List<String> content = new ArrayList<>();
		content.add("Update the details of the Library");
		content.add("Add copies of Book to the Branch");
		content.add("Quit to previous");
		IOTool.displayMenu("Librarian Mode\n", content);
		
		Integer choice = IOTool.readDigit(System.in, 1, content.size());
		switch(choice) {
			case 1:
				updateBranch(branchId, branchName);
				break;
			case 2:
				addCopiesOfBook(branchId, branchName);
				break;
			default:
				librarianMenu2();
		}
	}
	
	private void updateBranch(int branchId, String branchName) {
		System.out.printf("Librarian Mode%n%nYou have chosen to update the Branch with Branch Id: %d and Branch Name: %s. Enter ‘quit’ at any prompt to cancel operation.%n%n", 
				branchId, branchName);
		
		System.out.println("Please enter new branch name or enter N/A for no change:\n<take input>");
		String bName = IOTool.readString(System.in);
		if(bName.toLowerCase().equals("quit")) {
			librarianMenu3(branchId, branchName);
			return;
		}
		
		System.out.println("Please enter new branch address or enter N/A for no change:\n<take input>");
		String bAddress = IOTool.readString(System.in);
		if(bAddress.toLowerCase().equals("quit")) {
			librarianMenu3(branchId, branchName);
			return;
		}
		
		HashMap<String, String> whereKV = new HashMap<>();
		whereKV.put("branchId", Integer.toString(branchId));
		
		if((bName.equals("N/A") || bName.equals("N/a") || bName.equals("n/A") || bName.equals("n/a")) && 
				(bAddress.equals("N/A") || bAddress.equals("N/a") || bAddress.equals("N/a") || bAddress.equals("n/a"))) {
			System.out.println("\nNo changes.\n");
		}
		else if(bName.equals("N/A") || bName.equals("N/a") || bName.equals("n/A") || bName.equals("n/a")) {
			HashMap<String, String> setKV = new HashMap<>();
			setKV.put("branchAddress", bAddress);
			IOTool.getUpdateQuery(lms.getConnection(), "tbl_library_branch", setKV, whereKV);
		}
		else if(bAddress.equals("N/A") || bAddress.equals("N/a") || bAddress.equals("N/a") || bAddress.equals("n/a")){
			HashMap<String, String> setKV = new HashMap<>();
			setKV.put("branchName", bName);
			IOTool.getUpdateQuery(lms.getConnection(), "tbl_library_branch", setKV, whereKV);
		}
		else {
			HashMap<String, String> setKV = new HashMap<>();
			setKV.put("branchName", bName);
			setKV.put("branchAddress", bAddress);
			IOTool.getUpdateQuery(lms.getConnection(), "tbl_library_branch", setKV, whereKV);
		}
		librarianMenu3(branchId, branchName);
	}
	
	private void addCopiesOfBook(int branchId, String branchName) {
		try {
			String selectQuery = "select b.bookId, b.title, a.authorName from tbl_library_branch lb inner join tbl_book_copies bc on lb.branchId = bc.branchId inner join tbl_book b on bc.bookId = b.bookId inner join tbl_book_authors ba on b.bookId = ba.bookId inner join tbl_author a on ba.authorId = a.authorId where lb.branchId = " + branchId;
			PreparedStatement pstmt = lms.getConnection().prepareStatement(selectQuery);
			ResultSet rs = pstmt.executeQuery();
			List<String> content = new ArrayList<>();
			List<Integer> bookIds = new ArrayList<>();
			while(rs.next()) {
				String ss = String.format("%s by %s", rs.getString("title"), rs.getString("authorName"));
				content.add(ss);
				bookIds.add(rs.getInt("bookId"));
			}
			content.add("Quit to cancel operation");
			IOTool.displayMenu("Librarian Mode\n", content);
			
			Integer choice = IOTool.readDigit(System.in, 1, content.size());
			if(choice == content.size()) {
				pstmt.close();
			}
			else {
				int bookId = bookIds.get(choice-1);
				selectQuery = "select noOfCopies from tbl_book_copies bc where bc.branchId = " + branchId + " and bc.bookId = " + bookId;
				rs = pstmt.executeQuery(selectQuery);
				if(rs.next()) {
					System.out.printf("Existing number of copies: %d%n%n", rs.getInt("noOfCopies"));
				}
				else {
					System.out.println("Existing number of copies: 0\n");
				}
				System.out.println("Enter new number of copies:\n\n<take input>");
				pstmt.close();
				Integer numOfCopies = IOTool.readInteger(System.in);
				
				HashMap<String, String> setKV = new HashMap<>();
				setKV.put("noOfCopies", Integer.toString(numOfCopies));
				
				HashMap<String, String> whereKV = new HashMap<>();
				whereKV.put("bookId", Integer.toString(bookId));
				whereKV.put("branchId", Integer.toString(branchId));
				
				IOTool.getUpdateQuery(lms.getConnection(), "tbl_book_copies", setKV, whereKV);
			}
			librarianMenu3(branchId, branchName);
		} catch (SQLException e) {
			System.err.println("SQL Exception when selecting book by author");
			System.exit(0);
		}
	}
}