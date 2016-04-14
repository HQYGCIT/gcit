package com.gcit.lms.dao;

import java.util.List;
import com.gcit.lms.entity.Book;

public class LibraryBranch {
	private int branchId;
	private String branchName;
	private String branchAddress;
	private List<Book> books;
	
	public int getBranchId() {
		return branchId;
	}
	
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
	
	public String getBranchName() {
		return branchName;
	}
	
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	public String getBranchAddress() {
		return branchAddress;
	}
	
	public void setBranchAddress(String branchAddress) {
		this.branchAddress = branchAddress;
	}
	
	public List<Book> getBooks() {
		return books;
	}
	
	public void setBooks(List<Book> books) {
		this.books = books;
	}
}