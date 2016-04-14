
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class BaseDAO {
	private String driver = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://localhost/library";
	private String username = "root";
	private String password = "";
	
	protected Connection getConnection() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e1) {
			System.err.println("Class com.mysql.jdbc.Driver is not found.");
			System.exit(0);
		}
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			System.err.println("SQL Exception when getting a connection");
			System.exit(0);
		}
		return null;
	}
	
	public void save(String query, Object[] values) {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(query);
			for(int i=0; i<values.length; i+=1) {
				pstmt.setObject(i+1, values[i]);
			}
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("SQL Exception when saving changes");
			System.exit(0);
		}
	}
	
	public ResultSet readAll(String query, Object[] values) {
		Connection conn = getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(query);
			for(int i=0; i<values.length; i+=1) {
				pstmt.setObject(i+1, values[i]);
			}
			rs = pstmt.executeQuery();
			return rs;
		} catch (SQLException e) {
			System.err.println("SQL Exception when saving changes");
			System.exit(0);
		}
		return rs;
	}

	public <T> void printList(List<T> l) {
		for(int i=0; i<l.size(); i+=1) {
			System.out.printf("%d) %s%n", i+1, l.get(i));
		}
	}
}