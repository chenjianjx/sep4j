package org.sep4j.support;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class SepStringHelper {

	/**
	 * firstName => First Name
	 * @param camelStr
	 * @return
	 */
	public static String camelCaseToCapitalizedWords(String camelStr) {
		if (StringUtils.isBlank(camelStr)) {
			return camelStr;
		}

		String[] wordArray = StringUtils.splitByCharacterTypeCamelCase(camelStr);
		if (wordArray == null || wordArray.length == 0) {
			return null;
		}

		List<String> capitalizedList = Arrays.stream(wordArray).map(w -> StringUtils.capitalize(w))
				.collect(Collectors.toList());
		return StringUtils.join(capitalizedList, " ");
	}

	/**
	 * First Name => firstName
	 * @param words
	 * @return
	 */
	public static String wordsToUncapitalizedCamelCase(String words) {
		if (StringUtils.isBlank(words)) {
			return words;
		}		
		String[] wordArray = StringUtils.split(words);
		List<String> capitalizedList = Arrays.stream(wordArray).map(w -> StringUtils.capitalize(w))
				.collect(Collectors.toList());
		String camel = StringUtils.join(capitalizedList, "");
		return StringUtils.uncapitalize(camel);
	}
}
