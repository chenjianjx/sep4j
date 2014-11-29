package org.sep4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author chenjianjx
 * 
 */

public class SepExcelUtilsTest {

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void shouldSaveTest() {
		Assert.assertTrue(SepExcelUtils.shouldSave(null, true));
		Assert.assertTrue(SepExcelUtils.shouldSave(Arrays.asList(new DatumError()), true));

		Assert.assertTrue(SepExcelUtils.shouldSave(null, false));
		Assert.assertTrue(SepExcelUtils.shouldSave(new ArrayList<DatumError>(), false));
		Assert.assertFalse(SepExcelUtils.shouldSave(Arrays.asList(new DatumError()), false));
	}

	@Test
	public void validateRecordClass_NullClass() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		SepExcelUtils.validateRecordClass(null);
	}

	@Test
	public void createRecordIndexTest() {
		SepExcelUtils.createRecordInstance(DefaultConstructorBean.class);
		SepExcelUtils.createRecordInstance(PrivateClassBean.class);
	}

	@Test
	public void createRecordIndexTest_NoDefaultConstructor() {
		expectedEx.expect(RuntimeException.class);
		expectedEx.expectMessage("<init>()");
		SepExcelUtils.createRecordInstance(NoDefaultConstructorBean.class);
	}
	
	
	@Test
	public void validateReverseHeaderMapTest_Null(){
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		SepExcelUtils.validateReverseHeaderMap(null);
	}
	
	
	@Test
	public void validateReverseHeaderMapTest_Empty(){
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("empty");
		Map<String, String> map = new HashMap<String, String>();
		SepExcelUtils.validateReverseHeaderMap(map);
	}
	
	
	@Test
	public void validateReverseHeaderMapTest_Positive(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("someText", "someProp");
		SepExcelUtils.validateReverseHeaderMap(map);
	}
	
	
	@Test
	public void validateReverseHeaderMapTest_TextBlank(){
		Map<String, String> map = new HashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank headerText");
		expectedEx.expectMessage("1");
		
		map.put("text1", "prop1");
		map.put("	", "prop2");
		SepExcelUtils.validateReverseHeaderMap(map);
	}

	
	@Test
	public void validateReverseHeaderMapTest_PropBlank(){
		Map<String, String> map = new HashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank propName");
		expectedEx.expectMessage("1");
		
		map.put("text1", "prop1");
		map.put("text2", "	");
		SepExcelUtils.validateReverseHeaderMap(map);
	}
	
	
	@Test
	public void validateHeaderMapTest_Null(){
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("null");
		SepExcelUtils.validateHeaderMap(null);
	}
	
	
	@Test
	public void validateHeaderMapTest_Empty(){
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("empty");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		SepExcelUtils.validateHeaderMap(map);
	}
	
	
	@Test
	public void validateHeaderMapTest_Positive(){
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("someProp", "someText");
		SepExcelUtils.validateHeaderMap(map);
	}
	
	
	@Test
	public void validateHeaderMapTest_PropBlank(){
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("blank propName");
		expectedEx.expectMessage("1");
		
		map.put("prop1", "text1");
		map.put("	", "text2");
		SepExcelUtils.validateHeaderMap(map);
	}
	
	
	
	/**
	 * create one workbook and then parse it, excepting the final java list
	 * equals to the original one
	 * 
	 * @throws IOException
	 */
	@Test
	public void saveAndParseTest() throws Exception {
		// the map
		final LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put("id", "User ID");
		headerMap.put("username", "User Name");
		headerMap.put("alias", "Alias/别名");
		headerMap.put("birthDay", "Birth Day");
		headerMap.put("formattedBirthDay", "Formatted Birth Day");
		headerMap.put("balance", "Account Balance");
		headerMap.put("fakeProp", null);

		// the records
		final List<User> records = new ArrayList<User>();
		records.add(User.createInstance(1, null, "David Beckham", new Date(), new BigDecimal(100.00)));
		records.add(User.createInstance(2, "ronaldo", null, new Date(), new BigDecimal(231)));
		records.add(User.createInstance(3, "lifeifeng", "李玮峰", null, BigDecimal.ZERO));
		records.add(User.createInstance(4, "Bak Ji-seong", "박지성", new Date(), new BigDecimal(59)));

		// save it
		doSave(new SaveCallback() {
			public void doIt(OutputStream outputStream, List<DatumError> dataErrors) throws IOException {
				SepExcelUtils.save(headerMap, records, outputStream);
			}
		});

		// save it
		doSave(new SaveCallback() {
			public void doIt(OutputStream outputStream, List<DatumError> dataErrors) throws IOException {
				SepExcelUtils.save(headerMap, records, outputStream, "!!ERROR-A!!");
			}
		});

		// save it
		doSave(new SaveCallback() {
			public void doIt(OutputStream outputStream, List<DatumError> dataErrors) throws IOException {
				SepExcelUtils.save(headerMap, records, outputStream, "!!ERROR-B!!", dataErrors);
			}
		});

		// save it
		doSave(new SaveCallback() {
			public void doIt(OutputStream outputStream, List<DatumError> dataErrors) throws IOException {
				SepExcelUtils.saveIfNoDatumError(headerMap, records, outputStream, "!!ERROR-C!!", dataErrors);
			}
		});

	}

	@Test
	public void parseTest() throws Exception {
		// the reversed map
		final LinkedHashMap<String, String> reverseHeaderMap = new LinkedHashMap<String, String>();
		reverseHeaderMap.put("User ID", "id");
		reverseHeaderMap.put("User Name", "username");
		reverseHeaderMap.put("Alias/别名", "alias");
		reverseHeaderMap.put("Birth Day", "birthDay");
		reverseHeaderMap.put("Formatted Birth Day", "formattedBirthDay");
		reverseHeaderMap.put("Account Balance", "balance");
		reverseHeaderMap.put("Fake Prop", "fakeProp");

		List<CellError> cellErrors = new ArrayList<CellError>();
		InputStream inputStream = this.getClass().getResourceAsStream("/sample-excel.xlsx");
		// List<User> records = SepExcelUtils.parse(reverseHeaderMap,
		// inputStream, cellErrors, User.class);

		List<User> records = SepExcelUtils.parseIgnoringErrors(reverseHeaderMap, inputStream, User.class);
		for (User user : records) {
			System.out.println(user);
		}

		for (CellError cellError : cellErrors) {
			System.out.println(cellError);
		}

	}

	private void doSave(SaveCallback callback) throws Exception {
		FileOutputStream outputStream = null;
		try {
			File outFile = createFile();
			List<DatumError> dataErrors = new ArrayList<DatumError>();
			outputStream = new FileOutputStream(outFile);
			callback.doIt(outputStream, dataErrors);
			for (DatumError de : dataErrors) {
				System.out.println(de);
			}
			System.out.println("check " + outFile);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}

	private File createFile() {
		File outDir = new File(System.getProperty("user.home") + "/temp/sep");
		outDir.mkdirs();
		File outFile = new File(outDir, "out" + System.currentTimeMillis() + ".xlsx");
		return outFile;
	}

	private static interface SaveCallback {
		public void doIt(OutputStream outputStream, List<DatumError> dataErrors) throws Exception;
	}

	public static final class User {
		private long id;
		/**
		 * all ascii
		 */
		private String username;
		/**
		 * may contain non-ascii characters
		 */
		private String alias;
		private Date birthDay;
		private BigDecimal balance;

		public static final User createInstance(long id, String username, String alias, Date birthDay, BigDecimal balance) {
			User instance = new User();
			instance.id = id;
			instance.username = username;
			instance.alias = alias;
			instance.birthDay = birthDay;
			instance.balance = balance;
			return instance;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public Date getBirthDay() {
			return birthDay;
		}

		public String getFormattedBirthDay() {
			if (birthDay == null) {
				return null;
			}
			return DateFormatUtils.format(birthDay, "yyyy-MM-dd");
		}

		public void setFormattedBirthDay(String s) {
			if (s == null) {
				return;
			}
			try {
				Date d = DateUtils.parseDate(s, new String[] { "yyyy-MM-dd" });
				this.setBirthDay(d);
			} catch (ParseException e) {
				return;
			}
		}

		public void setBirthDay(Date birthDay) {
			this.birthDay = birthDay;
		}

		public BigDecimal getBalance() {
			return balance;
		}

		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

	private static class PrivateClassBean {

	}

	public static class DefaultConstructorBean {

	}

	public static class NoDefaultConstructorBean {
		public NoDefaultConstructorBean(Object param) {
		}
	}

}
