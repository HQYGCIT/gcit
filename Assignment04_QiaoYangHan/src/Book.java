public class Book {
	private int bookId;
	private String title;
	private String authorName;
	
	public Book(int bookId, String title, String authorName) {
		this.bookId = bookId;
		this.title = title;
		this.authorName = authorName;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String toString() {
		if(authorName == null) {
			return getTitle();
		}
		return getTitle() + " by " + getAuthorName();
	}
}