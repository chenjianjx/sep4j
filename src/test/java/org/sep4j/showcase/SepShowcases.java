package org.sep4j.showcase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.sep4j.ExcelUtils;

/**
 * for documentation only
 * 
 * @author chenjianjx
 */
public class SepShowcases {

	public static void main(String[] args) throws IOException {
		basicParse();
	
	}
	
	private static void basicParse() throws IOException{
		File file = basicSave();
		InputStream inputStream = toByteArrayInputStreamAndClose(new FileInputStream(file));
		
		Map<String, String> reverseHeaderMap = new HashMap<String,String>();
		reverseHeaderMap.put("User Id", "userId");  //"User Id" is a column header in the excel."userId" is the corresponding property of User class.
		reverseHeaderMap.put("First Name", "firstName");
		reverseHeaderMap.put("Last Name","lastName");
		
		List<User> users = ExcelUtils.parseIgnoringErrors(reverseHeaderMap, inputStream, User.class);
		
		System.out.println(users);
	}
	

	private static File basicSave() throws IOException {



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
		headerMap.put("userId", "User Id");  //"userId" is a property of User class. "User Id" will be the corresponding column header in the excel.
		headerMap.put("firstName", "First Name");
		headerMap.put("lastName", "Last Name");
		
		ExcelUtils.save(headerMap, users, outputStream);
		
		
		
		
		byte[] excel = outputStream.toByteArray();
		File theFile = createFile("save");
		FileUtils.writeByteArrayToFile(theFile, excel);
		return theFile;

	}

	private static File createFile(String prefix) {
		File dir = new File(System.getProperty("user.home"), "/temp/sep");
		dir.mkdirs();
		String filename = prefix + System.currentTimeMillis() + ".xlsx";
		File file = new File(dir, filename);
		return file;
	}
	
	private static ByteArrayInputStream toByteArrayInputStreamAndClose(InputStream in) {
		try {
			byte[] bytes = IOUtils.toByteArray(in);
			return new ByteArrayInputStream(bytes);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}

	}


	@SuppressWarnings("unused")
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

		

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

}
