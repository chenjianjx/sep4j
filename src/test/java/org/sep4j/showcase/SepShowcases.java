package org.sep4j.showcase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.sep4j.ExcelUtils;

/**
 * for documentation only
 * 
 * @author chenjianjx
 */
public class SepShowcases {

	public static void main(String[] args) throws IOException {
		basicSave();
	}

	private static void basicSave() throws IOException {



		User user1 = new User();
		user1.setUserId(1);
		user1.setFirstName("Lei");
		user1.setLastName("Li");
		
		
		
		User user2 = new User();
		user2.setUserId(2);
		user2.setFirstName("Jim");
		user2.setLastName("Green");

		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		Collection<User> users = Arrays.asList(user1, user2);
		LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put("userId", "User Id");  //userId is the property of User class
		headerMap.put("firstName", "First Name");
		headerMap.put("lastName", "Last Name");
		
		ExcelUtils.save(headerMap, users, outputStream);
		
		
		
		
		byte[] excel = outputStream.toByteArray();
		FileUtils.writeByteArrayToFile(createFile("save"), excel);

	}

	private static File createFile(String prefix) {
		File dir = new File(System.getProperty("user.home"), "/temp/sep");
		dir.mkdirs();
		String filename = prefix + System.currentTimeMillis() + ".xlsx";
		File file = new File(dir, filename);
		return file;
	}

	private static final class User {

		private long userId;
		private String firstName;
		private String lastName;

		public long getUserId() {
			return userId;
		}

		public void setUserId(long userId) {
			this.userId = userId;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

	}

}
