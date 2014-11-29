##sep4j
---
###Basic Examples

####Save(Write)
```java

		Collection<User> users = Arrays.asList(user1, user2);
		LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put("userId", "User Id");  //"userId" is a property of User class.
							// "User Id" will be the column header in the excel.
		headerMap.put("firstName", "First Name");
		headerMap.put("lastName", "Last Name");
		
		ExcelUtils.save(headerMap, users, outputStream);

```
You will get an excel file like 

|User Id|First Name|Last Name|
|-------|----------|---------|
|1		|Lei		|Li		|
|2		|Jim		|Green	|

Note: All cells generated will be String-Typed Cells. 

####Parse(Read)

```java
		Map<String, String> reverseHeaderMap = new HashMap<String,String>();
		reverseHeaderMap.put("User Id", "userId");  //"User Id" is a column header in the excel.
								//"userId" is the corresponding property of User class.
		reverseHeaderMap.put("First Name", "firstName");
		reverseHeaderMap.put("Last Name","lastName");
		
		List<User> users = ExcelUtils.parseIgnoringErrors(reverseHeaderMap, inputStream, User.class);
```
 
---
###Error Handling
####Save

```java
				
		List<DatumError> datumErrors = new ArrayList<DatumError>(); //to collect the errors
		headerMap.put("fakeProperty", "Fake Property"); //try to write an non-existing property
		ExcelUtils.save(headerMap, users, outputStream, "!!ERROR!!", datumErrors); 		
		for (DatumError de : datumErrors) {//here to handle the errors
			System.err.println(MessageFormat.format("Error: recordIndex = {0}, propName = \"{1}\", cause = {2}", de.getRecordIndex(), de.getPropName(), de.getCause()));			
		}
```		

Will then get an excel file like 

|User Id|First Name|Last Name|Fake Property|
|-------|----------|---------|-------------|
|1		|Lei		|Li		|!!ERROR!!	|
|2		|Jim		|Green	|!!ERROR!!	|

####Parse

```java
		List<CellError> cellErrors = new ArrayList<CellError>();
		try{			
			List<User> users = ExcelUtils.parse(reverseHeaderMap, inputStream, cellErrors, User.class);
		}catch (InvalidFormatException e) {
			System.err.println("Not a valid excel file");
		} catch (InvalidHeaderRowException e) {
			System.err.println("The column headers of your excel file do not match what we need");
		}		
		for (CellError ce : cellErrors) {
			System.err.println(MessageFormat.format("failed to parse a cell: rowIndexOneBased = {0}, columnIndexOneBased = {1}, propName = \"{2}\", headerText = \"{3}\", cause = {4} ", 
					ce.getRowIndexOneBased(),ce.getColumnIndexOneBased(), ce.getPropName(),ce.getHeaderText(), ce.getCause()));
		}
```
---

###Type Conversions
####Save
sep4j will call the properties' toString() methods to convert a property value to a String, and then write them to an excel file as String-typed cells.

* What if I want the property printed another way instead of toString(), for example, to format a date in Chinese style?   
** Create a new, String-typed property in your class by adding a getter method. 

```java

	private static final class User {
		...	
		public String getBirthDayString(){
			if(birthDay == null){
				return null;
			}			
			return DateFormatUtils.format(birthDay, "yyyy-MM-dd");			
		}
		...	
	}
```  

Also, add it to the header map: 
```java
		headerMap.put("birthDayString", "Birth Date");
```    

* Can I let sep4j produce Numeric-typed cells or another type others than String ? 
..* No, you can't. This is how sep4j keeps itself simple.

####Parse

* sep4j will only take cells of the following types. Cells of other types such as formula, blank etc. will be parsed as null values. 
..* String
..* Boolean
..* Numeric 
..* Date (Actually it is a Numeric cell type + Date cell style)  

* What if a cell is of String type in Excel, but its corresponding java property is of double? 
..* sep4j will do a guess for you, if the String's format in the cell is a valid number; You don't need another setter. if the String's format in the cell is not a valid number, sep4j will report a CellError saying "no suitable setter" 

* A property of my class is not of any basic types. For example, it's of List<String>.  What to do?
..* Add a String-Typed setter to your class 

```java
		public void setRoles(String rolesString){
			String[] roleArray = StringUtils.split(rolesString, ",");
			this.setRoles(Arrays.asList(roleArray));
		}
```

* Null handling
..* Cell with null value will lead to a null property value.  However, if the property is of primitive type such as "int", "long", then a CellError will be raised.

---

###Misc

####Best Practice for Date-typed properties during parsing
A date column may have both String-typed cells and Date-typed cells. You need to accommodate both. 

```java

		/**
		 * if the cell is of Date type 
		 * @param birthDay
		 */
		public void setBirthDay(Date birthDay) {
			this.birthDay = birthDay;
		}
		
		/**
		 * if the cell is of String type
		 * @param birthDayString
		 * @throws ParseException
		 */
		public void setBirthDay(String birthDayString) throws ParseException {
			if(birthDayString == null){
				return;
			}
			birthDay = DateUtils.parseDate(birthDayString, new String[]{"yyyy-MM-dd"});			
		}
		
``` 
