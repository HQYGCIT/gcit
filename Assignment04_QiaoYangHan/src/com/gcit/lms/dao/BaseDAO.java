package com.gcit.lms.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class BaseDAO {
	private String driver = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://localhost/library";
	private String username = "root";
	private String password = "";
	
	private Connection conn;
	
	public BaseDAO(Connection conn) {
		this.conn = conn;
	}
	
	public Connection getConnection(){
		return conn;
	}
	
	//select count(*) from table where like "?"
	public Integer getCount(String query) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when getting count.");
		}
		return null;
	}
	
	public void save(String query, Object[] values) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			for(int i=0; i<values.length; i+=1) {
				pstmt.setObject(i+1, values[i]);
			}
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("SQL Exception when saving changes.");
			System.exit(0);
		}
	}
	
	public Integer saveGetId(String query, Object[] values) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			for(int i=0; i<values.length; i++) {
				pstmt.setObject(i+1, values[i]);
			}
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when saving with id.");
			System.exit(0);
		}
		return null;
	}
	
	public List<?> readAll(String query, Object[] values) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			for(int i=0; i<values.length; i+=1) {
				pstmt.setObject(i+1, values[i]);
			}
			ResultSet rs = pstmt.executeQuery();
			return extractData(rs);
		} catch (SQLException e) {
			System.err.println("SQL Exception when saving changes.");
			System.exit(0);
		}
		return null;
	}
	
	public List<?> readFirstLevel(String query, Object[] values) {
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			return extractDataFirstLevel(rs);
		} catch (SQLException e) {
			System.err.println("SQL Exception when reading all first level.");
		}
		return null;
	}
	
	protected abstract List<?> extractData(ResultSet rs);
	
	protected abstract List<?> extractDataFirstLevel(ResultSet rs);
}