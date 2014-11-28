package org.sep4j.support;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author chenjianjx
 * 
 * 
 */
public class SepReflectionHelperTest {

	@Test
	public void findGetLiterallyTest() {
		Assert.assertNotNull(SepReflectionHelper.findGetLiterally(Book.class, "id"));
		Assert.assertNotNull(SepReflectionHelper.findGetLiterally(Book.class, "pageCount"));
		Assert.assertNotNull(SepReflectionHelper.findGetLiterally(Book.class, "authorAlive"));
		
		
		Assert.assertNull(SepReflectionHelper.findGetLiterally(Book.class, "zero"));
		Assert.assertNull(SepReflectionHelper.findGetLiterally(Book.class, "published"));
	}

	public void findIsLiterallyForBooleanTest() {
		Assert.assertNotNull(SepReflectionHelper.findIsLiterallyForBoolean(Book.class, "published"));
		Assert.assertNull(SepReflectionHelper.findIsLiterallyForBoolean(Book.class, "authorAlive"));
		
		
		
	}

	private static final class Book {
		private Long id;
		private String name;
		private String author;
		private int pageCount;
		private Boolean authorAlive;
		private boolean published;

		public Long getId() {
			return id;
		}

		// not a real getter
		public Long getzero() {
			return 0l;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public int getPageCount() {
			return pageCount;
		}

		public void setPageCount(int pageCount) {
			this.pageCount = pageCount;
		}

		public Boolean getAuthorAlive() {
			return authorAlive;
		}

		public void setAuthorAlive(Boolean authorAlive) {
			this.authorAlive = authorAlive;
		}

		public boolean isPublished() {
			return published;
		}

		public void setPublished(boolean published) {
			this.published = published;
		}

	}

}
