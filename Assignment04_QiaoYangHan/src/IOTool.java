import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOTool {
	public static void displayMenu(String head, List<String> content) {
		System.out.println(head);
		for(int i=0; i<content.size(); i+=1) {
			System.out.printf("%d) %s%n", i+1, content.get(i));
		}
	}
	
	//good
	private static boolean isInteger(String input) {
		try { 
			Integer.parseInt(input);
		} catch(NumberFormatException nfe) {
			return false;  
		}
		return true;
	}
	
	//good
	public static Integer readInteger() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		try {
			input = br.readLine().trim();
			if(input.toLowerCase().equals("exit")) {
				System.out.println("Good bye.");
				System.exit(0);
			}
			while(!isInteger(input)) {
				System.out.println("Your input is not an integer. Please enter an integer:");
				input = br.readLine().trim();
				if(input.toLowerCase().equals("exit")) {
					System.out.println("Good bye.");
					System.exit(0);
				}
			}
		} catch (IOException e) {
			System.err.println("IO Exception when reading an integer.");
			System.exit(0);
		}
		return Integer.parseInt(input);
	}
		
	//good
	public static Integer readInteger(int start, int end) {
		Integer number = readInteger();
		while(number < start || number >= end) {
			System.out.printf("Your integer is not in range. Please enter a number from %d to %d:%n", start, end-1);
			number = readInteger();
		}
		return number;
	}
	
	public static String readString(InputStream in) {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String s = null;
		try {
			s = br.readLine().trim();
			if(s.toLowerCase().equals("exit")) {
				System.out.println("Good bye");
				System.exit(0);
			}
			while(s.equals("")) {
				System.out.println("Please enter a string that is not empty or blank.");
				s = br.readLine().trim();
				if(s.toLowerCase().equals("exit")) {
					System.out.println("Good bye");
					System.exit(0);
				}
			}
			return s;
		} catch (IOException e) {
			System.err.println("IO Exception when reading a string");
			System.exit(0);
		}
		return s;
	}
	
	public static void getUpdateQuery(Connection conn, String table, Map<String, String> setKV, Map<String, String> whereKV) {
		StringBuilder setClause = new StringBuilder();
		for(Map.Entry<String, String> entry : setKV.entrySet()) {
			setClause.append(entry.getKey() + "=\"" + entry.getValue() + "\", ");
		}
		setClause.delete(setClause.length()-2, setClause.length());
		
		StringBuilder whereClause = new StringBuilder();
		for(Map.Entry<String, String> entry : whereKV.entrySet()) {
			whereClause.append(entry.getKey() + "=\"" + entry.getValue() + "\" and ");
		}
		whereClause.delete(whereClause.length()-5, whereClause.length());
		
		String update = String.format("update %s set %s where %s", table, setClause.toString(), whereClause.toString());

		try {
			PreparedStatement pstmt = conn.prepareStatement(update);
			pstmt.executeUpdate();
			System.out.println("\nSuccessfully updated.\n");
			pstmt.close();
		} catch (SQLException e) {
			System.err.println("SQL Exception when updating.");
			System.exit(0);
		}
	}
	
	public static String getInsertQuery(String table, Map<String, String> KV) {
		StringBuilder insert = new StringBuilder();
		insert.append("insert into " + table + " (");
		
		StringBuilder keys = new StringBuilder();
		StringBuilder values = new StringBuilder();
		
		for(Map.Entry<String, String> entry : KV.entrySet()) {
			keys.append(entry.getKey() + ", ");
			values.append("\"" + entry.getValue() + "\", ");
		}
		
		if(KV.size() > 0) {
			keys.delete(keys.length()-2, keys.length());
			values.delete(values.length()-2, values.length());
		}
		insert.append(keys.toString() + ") values (");
		insert.append(values.toString() + ")");
		return insert.toString();
	}
	
	public static String getUpdateQuery(String table, Map<String, String> setKV, Map<String, String> whereKV) {
		StringBuilder setClause = new StringBuilder();
		for(Map.Entry<String, String> entry : setKV.entrySet()) {
			setClause.append(entry.getKey() + "=\"" + entry.getValue() + "\", ");
		}
		if(setKV.size() > 0) {
			setClause.delete(setClause.length()-2, setClause.length());
		}
		
		StringBuilder whereClause = new StringBuilder();
		for(Map.Entry<String, String> entry : whereKV.entrySet()) {
			whereClause.append(entry.getKey() + "=\"" + entry.getValue() + "\" and ");
		}
		if(whereKV.size() > 0) {
			whereClause.delete(whereClause.length()-5, whereClause.length());
		}
		
		String update = String.format("update %s set %s where %s", table, setClause.toString(), whereClause.toString());
		return update;
	}
	
	public static String getDeleteQuery(String table, Map<String, String> whereKV) {
		StringBuilder delete = new StringBuilder();
		delete.append("delete from " + table + " where ");
		for(Map.Entry<String, String> entry : whereKV.entrySet()) {
			delete.append(entry.getKey() + "=\"" + entry.getValue() + "\" and ");
		}
		if(whereKV.size() > 0) {
			delete.delete(delete.length()-5, delete.length());
		}
		return delete.toString();
	}
	
	public static Map<String, String> getKV(List<String> columnNames) {
		Map<String, String> kv = new HashMap<>();
		for(String key : columnNames) {
			System.out.printf("Enter a value to %s or N/A to skip:%n", key);
			String value = readString(System.in);
			if(value.equals("n/a") || value.equals("N/a") || value.equals("n/A") || value.equals("N/A")) {
				continue;
			}
			else {
				kv.put(key, value);
			}
		}
		return kv;
	}
	
	public static List<String> getValuesToColumns(List<String> columnNames) {
		List<String> values = new ArrayList<>();
		for(int i=0; i<columnNames.size(); i+=1) {
			String value = null;
			if(i == 0) {
				System.out.printf("Enter value to %s:%n", columnNames.get(i));
				value = readString(System.in);
				values.add(value);
			}
			else {
				System.out.printf("Enter value to %s or N/A to skip:%n", columnNames.get(i));
				value = readString(System.in);
				if(value.equals("n/a") || value.equals("N/a") || value.equals("n/A") || value.equals("N/A")) {
					continue;
				}
				values.add(value);
			}
		}
		return values;
	}
}