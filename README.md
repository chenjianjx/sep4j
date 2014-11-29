##sep4j

sep4j = Simplified Excel Processing for Java, with which you can write or read an excel file with a simple method call.

###Basic Examples

####Save(Write)
```java

		Collection<User> users = Arrays.asList(user1, user2);
		LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put("userId", "User Id");  //"userId" is a property of User class, "User Id" will be the column header in the excel.
		headerMap.put("firstName", "First Name");
		headerMap.put("lastName", "Last Name");
		
		ExcelUtils.save(headerMap, users, outputStream);

```
You will get excel file like 

|User Id|First Name|Last Name|
|1		|Lei		|Li		|
|2		|Jim		|Green	|

Note: All cells generated will be String-Typed Cells. 

####Parse(Read)

```java
		Map<String, String> reverseHeaderMap = new HashMap<String,String>();
		reverseHeaderMap.put("User Id", "userId");  //"User Id" is a column header in the excel."userId" is the corresponding property of User class.
		reverseHeaderMap.put("First Name", "firstName");
		reverseHeaderMap.put("Last Name","lastName");
		
		List<User> users = ExcelUtils.parseIgnoringErrors(reverseHeaderMap, inputStream, User.class);
```
 
