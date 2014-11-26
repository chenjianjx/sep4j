package org.sep4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;

/**
 * 
 * @author chenjianjx
 * 
 */
public class SepExcelUtilsTest {

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
		headerMap.put("fakeProp", "Fake Property");

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

		public void setBirthDay(Date birthDay) {
			this.birthDay = birthDay;
		}

		public BigDecimal getBalance() {
			return balance;
		}

		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}

	}

}
