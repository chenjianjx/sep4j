package org.sep4j.showcase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;
import org.sep4j.CellError;
import org.sep4j.DatumError;
import org.sep4j.InvalidHeaderRowException;
import org.sep4j.Ssio;

import com.google.common.collect.ImmutableMap;

/**
 * for documentation only
 * 
 * @author chenjianjx
 */
public class SepShowcases {

	@Test
	public void save() throws IOException {
		doSave();
	}

	@Test
	public void saveWithGuava() throws IOException {

		Collection<User> userList = buildTestUsers();
		ByteArrayOutputStream spreadsheetOutputStream = new ByteArrayOutputStream();

		Ssio.save(ImmutableMap.of("userId", "User Id", "firstName",
				"First Name", "lastName", "Last Name"), userList, spreadsheetOutputStream);

		byte[] spreadsheet = spreadsheetOutputStream.toByteArray();
		File theFile = createFile("saveWithGuava");
		FileUtils.writeByteArrayToFile(theFile, spreadsheet);
		System.out.println("File saved as " + theFile.getAbsolutePath());

	}

	@Test
	public void saveAndAppend() throws IOException {

		Collection<User> userList = buildTestUsers();

		File theFile = createFile("saveAndAppend");
		ImmutableMap<String, String> headerMap = ImmutableMap.of("userId", "User Id", "firstName",
				"First Name", "lastName", "Last Name");

		Ssio.save(headerMap, userList, theFile);
		System.out.println("File saved as " + theFile.getAbsolutePath());

		Collection<User> newListToAppend = buildTestUsers();
		Ssio.appendTo(headerMap, newListToAppend, theFile);
		System.out.println("Records appended to " + theFile.getAbsolutePath());
	}



	@Test
	public void saveMaps() throws IOException {
		ByteArrayOutputStream spreadsheetOutputStream = new ByteArrayOutputStream();

		ImmutableMap<String, Object> record1 = ImmutableMap.of("firstName", "Jim", "lastName", "Green");
		ImmutableMap<String, Object> record2 = ImmutableMap.of("firstName", "Li", "lastName", "Lei");
		List<Map<String,Object>> records = Arrays.asList(record1, record2);
		ImmutableMap<String, String> headerMap = ImmutableMap.of("firstName", "First Name", "lastName", "Last Name");
		Ssio.saveMaps(headerMap, records, spreadsheetOutputStream);

		//You can also let sep4j generate a header map for you, by just providing the map's keys
		Ssio.saveMaps(Arrays.asList("firstName", "lastName"), records, spreadsheetOutputStream);

		byte[] spreadsheet = spreadsheetOutputStream.toByteArray();
		File theFile = createFile("saveMaps");
		FileUtils.writeByteArrayToFile(theFile, spreadsheet);
		System.out.println("File saved as " + theFile.getAbsolutePath());
	}


	@Test
	public void saveWithGeneratedHeaderMap() throws IOException {

		Collection<User> userList = buildTestUsers();
		ByteArrayOutputStream spreadsheetOutputStream = new ByteArrayOutputStream();

		Ssio.save(User.class, userList, spreadsheetOutputStream);

		byte[] spreadsheet = spreadsheetOutputStream.toByteArray();
		File theFile = createFile("saveWithGeneratedHeaderMap");
		FileUtils.writeByteArrayToFile(theFile, spreadsheet);
		System.out.println("File saved as " + theFile.getAbsolutePath());
	}
	

	@Test
	public void parse() throws IOException {
		File file = doSave();
		InputStream spreadsheetInputStream = toByteArrayInputStreamAndClose(new FileInputStream(
				file));

		Map<String, String> reverseHeaderMap = new HashMap<String, String>();
		reverseHeaderMap.put("User Id", "userId"); // "User Id" is a column
													// header in the
													// spreadsheet."userId" is
													// the corresponding
													// property of User class.
		reverseHeaderMap.put("First Name", "firstName");
		reverseHeaderMap.put("Last Name", "lastName");

		List<CellError> cellErrors = new ArrayList<CellError>();
		try {
			@SuppressWarnings("unused")
			List<User> users = Ssio.parse(reverseHeaderMap, spreadsheetInputStream,
					cellErrors, User.class);
		} catch (InvalidFormatException e) {
			System.err.println("Not a valid spreadsheet file");
		} catch (InvalidHeaderRowException e) {
			System.err
					.println("The column headers of your spreadsheet file donot match what we need");
		}

		for (CellError ce : cellErrors) {
			System.err
					.println(MessageFormat
							.format("failed to parse a cell: rowIndexOneBased = {0}, columnIndexOneBased = {1}, propName = \"{2}\", headerText = \"{3}\", cause = {4} ",
									ce.getRowIndexOneBased(),
									ce.getColumnIndexOneBased(),
									ce.getPropName(), ce.getHeaderText(),
									ce.getCause()));
		}

		// List<User> users = Ssio.parseIgnoringErrors(reverseHeaderMap,
		// inputStream, User.class);

		// System.out.println(users);
	}

	@Test
	public void parseWithGuava() throws IOException {
		File file = doSave();
		InputStream spreadsheetInputStream = toByteArrayInputStreamAndClose(new FileInputStream(
				file));

		List<CellError> cellErrors = new ArrayList<CellError>();
		try {
			@SuppressWarnings("unused")
			List<User> users = Ssio.parse(ImmutableMap.of("User Id", "userId",
					"First Name", "firstName", "Last Name", "lastName"),
					spreadsheetInputStream, cellErrors, User.class);
		} catch (InvalidFormatException e) {
			System.err.println("Not a valid spreadsheet file");
		} catch (InvalidHeaderRowException e) {
			System.err
					.println("The column headers of your spreadsheet file donot match what we need");
		}

		for (CellError ce : cellErrors) {
			System.err
					.println(MessageFormat
							.format("failed to parse a cell: rowIndexOneBased = {0}, columnIndexOneBased = {1}, propName = \"{2}\", headerText = \"{3}\", cause = {4} ",
									ce.getRowIndexOneBased(),
									ce.getColumnIndexOneBased(),
									ce.getPropName(), ce.getHeaderText(),
									ce.getCause()));
		}

		// List<User> users = Ssio.parseIgnoringErrors(reverseHeaderMap,
		// inputStream, User.class);

		// System.out.println(users);
	}
	
	
	@Test
	public void parseWithGeneratedReverseHeaderMap() throws IOException {
		File file = doSave();
		InputStream inputStream = toByteArrayInputStreamAndClose(new FileInputStream(file));
		List<User> users = Ssio.parseIgnoringErrors(inputStream, User.class);
		System.out.println(users);	 
	}


	@Test
	public void parseMaps() throws IOException {
		File file = doSave();
		InputStream inputStream = toByteArrayInputStreamAndClose(new FileInputStream(file));
		Map<String, String> reverseHeaderMap = ImmutableMap.of("User Id", "userId",
				"First Name", "firstName", "Last Name", "lastName");
		List<Map<String, String>> users = Ssio.parseToMapsIgnoringErrors(reverseHeaderMap,  inputStream);
		System.out.println(users);
	}

	private File doSave() throws IOException {
		Collection<User> userList = buildTestUsers();

		ByteArrayOutputStream spreadsheetOutputStream = new ByteArrayOutputStream();
		Map<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put("userId", "User Id"); // "userId" is a property of User
											// class. "User Id" will be the
											// corresponding column header in
											// the spreadsheet.
		headerMap.put("firstName", "First Name");
		headerMap.put("lastName", "Last Name");
		headerMap.put("birthDay", "Birth Date");
		headerMap.put("birthDayString", "Birth Date");

		// Ssio.save(headerMap, users, outputStream);

		/*** show case error handling ***/

		// ////to collect the errors
		List<DatumError> datumErrors = new ArrayList<DatumError>();
		headerMap.put("fakeProperty", "Fake Property"); // try to write an
														// non-exsting property
		Ssio.save(headerMap, userList, spreadsheetOutputStream, "!!ERROR!!", datumErrors);
		for (DatumError de : datumErrors) {// here to handle the errors
			System.err
					.println(MessageFormat
							.format("Error: recordIndex = {0}, propName = \"{1}\", cause = {2}",
									de.getRecordIndex(), de.getPropName(),
									de.getCause()));
		}

		byte[] spreadsheet = spreadsheetOutputStream.toByteArray();
		File theFile = createFile("save");
		FileUtils.writeByteArrayToFile(theFile, spreadsheet);
		return theFile;
	}

	private Collection<User> buildTestUsers() {
		User user1 = new User();
		user1.setUserId(1);
		user1.setFirstName("Lei");
		user1.setLastName("Li");

		User user2 = new User();
		user2.setUserId(2);
		user2.setFirstName("Jim");
		user2.setLastName("Green");

		Collection<User> userList = Arrays.asList(user1, user2);
		return userList;
	}

	private File createFile(String prefix) {
		File dir = new File(System.getProperty("java.io.tmpdir"), "/sep4j/show-case");
		dir.mkdirs();
		String filename = prefix + System.currentTimeMillis() + ".xlsx";
		File file = new File(dir, filename);
		return file;
	}

	private ByteArrayInputStream toByteArrayInputStreamAndClose(InputStream in) {
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
		private Date birthDay;

		private List<String> roles;

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

		public Date getBirthDay() {
			return birthDay;
		}

		public String getBirthDayString() {
			if (birthDay == null) {
				return null;
			}
			return DateFormatUtils.format(birthDay, "yyyy-MM-dd");
		}

		/**
		 * if the cell is of Date type
		 * 
		 * @param birthDay
		 */
		public void setBirthDay(Date birthDay) {
			this.birthDay = birthDay;
		}

		/**
		 * if the cell is of String type
		 * 
		 * @param birthDayString
		 * @throws ParseException
		 */
		public void setBirthDay(String birthDayString) throws ParseException {
			if (birthDayString == null) {
				return;
			}
			birthDay = DateUtils.parseDate(birthDayString,
					new String[] { "yyyy-MM-dd" });
		}

		public List<String> getRoles() {
			return roles;
		}

		public void setRoles(List<String> roles) {
			this.roles = roles;
		}

		public void setRoles(String rolesString) {
			String[] roleArray = StringUtils.split(rolesString, ",");
			this.setRoles(Arrays.asList(roleArray));
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this,
					ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

}
