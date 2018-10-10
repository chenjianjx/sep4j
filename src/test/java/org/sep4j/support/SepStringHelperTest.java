package org.sep4j.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class SepStringHelperTest {

	@Test
	public void camelCaseToCapitalizedWordsTest() {
		assertNull(SepStringHelper.camelCaseToCapitalizedWords(null));
		assertEquals("", SepStringHelper.camelCaseToCapitalizedWords(""));
		assertEquals("  ", SepStringHelper.camelCaseToCapitalizedWords("  "));

		assertEquals("First Name", SepStringHelper.camelCaseToCapitalizedWords("firstName"));
		assertEquals("First Name", SepStringHelper.camelCaseToCapitalizedWords("FirstName"));

		assertEquals("Abc", SepStringHelper.camelCaseToCapitalizedWords("abc"));
		assertEquals("DEF", SepStringHelper.camelCaseToCapitalizedWords("DEF"));
		assertEquals("Abc DEF", SepStringHelper.camelCaseToCapitalizedWords("abcDEF"));
		assertEquals("Abc DE Fghi", SepStringHelper.camelCaseToCapitalizedWords("abcDEFghi"));

		assertEquals("Abc 200 Ghi", SepStringHelper.camelCaseToCapitalizedWords("abc200Ghi"));
		assertEquals("Abc 200 Ghi", SepStringHelper.camelCaseToCapitalizedWords("abc200ghi"));

		assertEquals("First _ Name", SepStringHelper.camelCaseToCapitalizedWords("first_name"));
	}

	@Test
	public void wordsToUncapitalizedCamelCaseTest() {
		assertNull(SepStringHelper.wordsToUncapitalizedCamelCase(null));
		assertEquals("", SepStringHelper.wordsToUncapitalizedCamelCase(""));
		assertEquals("  ", SepStringHelper.wordsToUncapitalizedCamelCase("  "));

		assertEquals("firstName", SepStringHelper.wordsToUncapitalizedCamelCase("First Name"));

		assertEquals("abc", SepStringHelper.wordsToUncapitalizedCamelCase("Abc"));
		assertEquals("dEF", SepStringHelper.wordsToUncapitalizedCamelCase("DEF"));
		assertEquals("abcDEF", SepStringHelper.wordsToUncapitalizedCamelCase("Abc DEF"));
		assertEquals("abcDEFGhi", SepStringHelper.wordsToUncapitalizedCamelCase("Abc DEF ghi"));

		assertEquals("abc200Ghi", SepStringHelper.wordsToUncapitalizedCamelCase("Abc 200 Ghi"));
		assertEquals("first_Name", SepStringHelper.wordsToUncapitalizedCamelCase("First _ Name"));
	}

}
