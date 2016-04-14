package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Publisher;

public class PublisherDAO extends BaseDAO {
	public PublisherDAO(Connection conn) {
		super(conn);
	}

	public void addPublisher(Publisher publisher) {
		save("insert into tbl_publisher (publisherName, publisherAddress, publisherPhone) values (?, ?, ?)", new Object[] {publisher.getPublisherName(), publisher.getPublisherAddress(), publisher.getPublisherPhone()});
	}
	
	public Integer addPublisherGetId(Publisher publisher) {
		return saveGetId("insert into tbl_publisher (publisherName, publisherAddress, publisherPhone) values (?, ?, ?)", new Object[] {publisher.getPublisherName(), publisher.getPublisherAddress(), publisher.getPublisherPhone()});
	}
	
	public void updatePublisher(Publisher publisher) {
		save("update tbl_publisher set publisherName = ?, publisherAddress = ?, publisherPhone = ? where publisherId = ?", new Object[] {publisher.getPublisherName(), publisher.getPublisherAddress(), publisher.getPublisherPhone(), publisher.getPublisherId()});
	}
	
	public void deletePublisher(Publisher publisher) {
		save("delete from tbl_publisher where publisherId = ?", new Object[] {publisher.getPublisherId()});
	}
	
	public List<Publisher> readAllPublishers() {
		return (List<Publisher>) readAll("select * from tbl_publisher", new Object[0]);
	}
	
	//needs work
	public List<Publisher> readPublishersByName(String name) {
		return (List<Publisher>) readAll("select * from tbl_publisher where publisherName like ?", new Object[] {name});
	}
	
	public Integer getCount() {
		return getCount("select count(*) from tbl_publisher");
	}
	
	@Override
	public List<Publisher> extractData(ResultSet rs) {
		List<Publisher> publishers = new ArrayList<Publisher>();
		BookDAO bDao = new BookDAO(getConnection());
		try {
			while(rs.next()) {
				Publisher p = new Publisher();
				p.setPublisherId(rs.getInt("publisherId"));
				p.setPublisherName(rs.getString("publisherName"));
				p.setPublisherAddress(rs.getString("publisherAddress"));
				p.setPublisherPhone(rs.getString("publisherPhone"));
				List<Book> books = (List<Book>) bDao.readFirstLevel("select * from tbl_book where pubId = ?)", new Object[] {p.getPublisherId()});
				p.setBooks(books);
				publishers.add(p);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when extracting data.");
			System.exit(0);
		}
		return publishers;
	}
	
	@Override
	public List<Publisher> extractDataFirstLevel(ResultSet rs) {
		List<Publisher> publishers = new ArrayList<Publisher>();
		try {
			while(rs.next()){
				Publisher p = new Publisher();
				p.setPublisherId(rs.getInt("publisherId"));
				p.setPublisherName(rs.getString("publisherName"));
				p.setPublisherAddress(rs.getString("publisherAddress"));
				p.setPublisherPhone(rs.getString("publisherPhone"));
				publishers.add(p);
			}
		} catch (SQLException e) {
			System.err.println("SQL Exception when extracting data first level.");
			System.exit(0);
		}
		return publishers;
	}
}