## Sep4j = Simple Spreadsheet Processing for Java
---

It's a wrapper of Apache POI, with which you can do javabeans <-> spreadsheet conversion even more easily.

```diff
- Note:  please use [ssio](https://github.com/chenjianjx/ssio) instead, annotation-based and strong-typed in cells
 
```
 

### Quick Start

#### pom.xml
```xml

<dependencies>
	<dependency>
		<groupId>com.github.chenjianjx</groupId>
		<artifactId>sep4j</artifactId>
		<version>2.0.5</version>
	</dependency>
	..		
</dependencies>	
```

#### Save(Write)
```java
		
Map<String, String> headerMap = new LinkedHashMap<String, String>();
headerMap.put("userId", "User Id"); // "userId" is a property of the javabeans you are going to save.
			     // "User Id" will be the corresponding column header in the spreadsheet.
headerMap.put("firstName", "First Name");
headerMap.put("lastName", "Last Name");

OutputStream spreadsheetOutputStream = new FileOutputStream("someExcelFile.xlsx");
Ssio.save(headerMap, userList, spreadsheetOutputStream);	
//"spreadsheetOutputStream" can be replaced with "spreadsheetOutputFile" (a java.io.File object) 	
```
or if you use Guava, you can just
```java
Ssio.save(
    ImmutableMap.of("userId", "User Id", "firstName","First Name", "lastName", "Last Name"), 
    userList, spreadsheetOutputStream);
```

You can even let the program generate a header map for you: 
````java
Ssio.save(User.class, userList, spreadsheetOutputStream);
````

You will get an spreadsheet file like 

|User Id|First Name|Last Name|
|-------|----------|---------|
|1		|Lei		|Li		|
|2		|Jim		|Green	|

Note: All cells generated will be String-Typed Cells. 

#### Parse(Read)

```java
Map<String, String> reverseHeaderMap = new HashMap<String,String>();
reverseHeaderMap.put("User Id", "userId");  //"User Id" is a column header in the spreadsheet.
					//"userId" is the corresponding property of User class.
reverseHeaderMap.put("First Name", "firstName");
reverseHeaderMap.put("Last Name","lastName");

InputStream spreadsheetInputStream = new FileInputStream("someExcelFile.xlsx");
List<User> users = Ssio.parseIgnoringErrors(reverseHeaderMap, spreadsheetInputStream, User.class); 
//"spreadsheetInputStream" can be replaced with "spreadsheetInputFile" (a java.io.File object) 	
```
or if you use Guava, you can just
```java
List<User> users = Ssio.parse(
    ImmutableMap.of("User Id","userId","First Name","firstName","Last Name","lastName"),
    spreadsheetInputStream,  User.class);
```
You can even let the program guess out a reverseHeaderMap for you
````java
List<User> users = Ssio.parseIgnoringErrors(spreadsheetInputStream, User.class);
````
 
---
### Error Handling
#### Save

```java
				
List<DatumError> datumErrors = new ArrayList<DatumError>(); //to collect the errors
headerMap.put("fakeProperty", "Fake Property"); //try to write an non-existing property
Ssio.save(headerMap, users, outputStream, "!!ERROR!!", datumErrors); 		
for (DatumError de : datumErrors) {//here to handle the errors
	System.err.println(MessageFormat.format("Error: recordIndex = {0}, 
	propName = \"{1}\", cause = {2}",
	de.getRecordIndex(), de.getPropName(), de.getCause()));			
}
```		

Will then get an spreadsheet file like 

|User Id|First Name|Last Name|Fake Property|
|-------|----------|---------|-------------|
|1		|Lei		|Li		|!!ERROR!!	|
|2		|Jim		|Green	|!!ERROR!!	|

#### Parse

```java
List<CellError> cellErrors = new ArrayList<CellError>();
try{			
	List<User> users = Ssio.parse(reverseHeaderMap, inputStream, cellErrors, User.class);
}catch (InvalidFormatException e) {
	System.err.println("Not a valid spreadsheet file");
} catch (InvalidHeaderRowException e) {
	System.err.println("The column headers of your spreadsheet file do not match what we need");
}		
for (CellError ce : cellErrors) {
	System.err.println(MessageFormat.format("failed to parse a cell: rowIndexOneBased = {0},
	columnIndexOneBased = {1}, propName = \"{2}\", headerText = \"{3}\", cause = {4} ", 
			ce.getRowIndexOneBased(),ce.getColumnIndexOneBased(),
			ce.getPropName(),ce.getHeaderText(), ce.getCause()));
}
```
---

### Type Conversions
#### Save
Sep4j will call the properties' toString() methods to convert a property value to a String, and then write them to an spreadsheet file as String-typed cells.

* What if I want the property printed another way instead of toString(), for example, to format a date in Chinese style? 
    - Create a new, String-typed property in your class by adding a getter method. 

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

* Can I let Sep4j produce Numeric-typed cells or another type others than String ? 
    - No, you can't. This is how Sep4j keeps itself simple.

#### Parse

* Sep4j will only take cells of the following types. Cells of other types such as formula, blank etc. will be parsed as null values. 
    - String
    - Boolean
    - Numeric 
    - Date (Actually it is a Numeric cell type + Date cell style)  

* What if a cell is of String type in the spreadsheet, but its corresponding java property is of double? 
    - Sep4j will do a guess for you, if the String's format in the cell is a valid number; You don't need another setter. if the String's format in the cell is not a valid number, Sep4j will report a CellError saying "no suitable setter" 

* A property of my class is not of any basic types. For example, it's of List<String>.  What to do?
    - Add a String-Typed setter to your class 

```java
public void setRoles(String rolesString){
	String[] roleArray = StringUtils.split(rolesString, ",");
	this.setRoles(Arrays.asList(roleArray));
}
```

* Null handling
    - Cell with null value will lead to a null property value.  However, if the property is of primitive type such as "int", "long", then a CellError will be raised.

---
### Advanced Usages

#### Deal with Maps instead of Javabeans 

In some cases you have a collection of Maps and you don't want to bother creating a class. This can help:  

##### Save
````
ImmutableMap<String, Object> record1 = ImmutableMap.of("firstName", "Jim", "lastName", "Green");
ImmutableMap<String, Object> record2 = ImmutableMap.of("firstName", "Li", "lastName", "Lei");
List<Map<String,Object>> records = Arrays.asList(record1, record2);
ImmutableMap<String, String> headerMap = ImmutableMap.of("firstName", "First Name", "lastName", "Last Name");
Ssio.saveMaps(headerMap, records, spreadsheetOutputStream);
````

You can also let sep4j generate a header map for you, by just providing the map's keys

````
Ssio.saveMaps(Arrays.asList("firstName", "lastName"), records, spreadsheetOutputStream);
````

#### Parse
````
Map<String, String> reverseHeaderMap = ImmutableMap.of("User Id", "userId",
				"First Name", "firstName", "Last Name", "lastName");
List<Map<String, String>> users = Ssio.parseToMapsIgnoringErrors(reverseHeaderMap,  inputStream);

````

#### Append records to a spreadsheet file
````
Ssio.appendTo(headerMap, newListToAppend, theFile);
````
----

### Misc

#### Best Practice for Date-typed properties during parsing
A date column in a spreadsheet may have both String-typed cells and Date-typed cells (common human error). You need to accommodate both. 

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
