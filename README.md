#sep4j

sep4j = Simplified Excel Processing for Java, with which you can write or read an excel file with a simple method call.

##Basic Examples

###Save(Write)
```java

		Collection<User> users = Arrays.asList(user1, user2);
		LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
		headerMap.put("userId", "User Id");  //userId is a property of User class
		headerMap.put("firstName", "First Name");
		headerMap.put("lastName", "Last Name");
		
		ExcelUtils.save(headerMap, users, outputStream);

```



###Parse(Read)
 
