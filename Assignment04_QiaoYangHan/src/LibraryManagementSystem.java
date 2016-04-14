import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibraryManagementSystem {
	
	public LibraryManagementSystem() {
	}
	
	public void mainMenu() {
		List<String> content = new ArrayList<>();
		content.add("Librarian");
		content.add("Administrator");
		content.add("Borrower");
		
		IOTool.displayMenu("Welcome to the GCIT Library Management System. Which category of a user are you\n", content);
		
	    Integer choice = IOTool.readInteger(1, content.size()+1);
		switch(choice) {
			case 1:
				break;
			case 2:
				break;
			case 3:
				Borrower b = new Borrower();
				b.borrowerMenu();
				break;
			default:
				System.out.println("Good bye.");
				System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		LibraryManagementSystem lms = new LibraryManagementSystem();
		lms.mainMenu();
	}
}