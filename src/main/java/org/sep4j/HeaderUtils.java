package org.sep4j;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.sep4j.support.SepBasicTypeConverts;
import org.sep4j.support.SepStringHelper;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class HeaderUtils {

	/**
	 * property "firstName" will lead to a map entry of "firstName" => "First Name"
	 * 
	 * @param beanClass
	 * @return A modifiable map.  Feel free to add or remove elements.
	 */
	public static Map<String, String> generateHeaderMapFromProps(Class<?> beanClass) {
		LinkedHashMap<String, String> resultMap = new LinkedHashMap<String, String>();

		PropertyDescriptor[] pdArray = PropertyUtils.getPropertyDescriptors(beanClass);
		if (pdArray == null || pdArray.length == 0) {
			return resultMap;
		}

		List<PropertyDescriptor> readablePdList = Arrays.stream(pdArray)
				.filter(pd -> !pd.getName().equals("class"))
				.filter(pd -> pd.getReadMethod() != null)
				.collect(Collectors.toList());
		List<String> finalProps = readablePdList.stream().map(pd -> pd.getName()).collect(Collectors.toList());

		for (String propName : finalProps) {
			String headerName = SepStringHelper.camelCaseToCapitalizedWords(propName);
			resultMap.put(propName, headerName);
		}

		return resultMap;
	}

	/**
	 * property "firstName" will lead to a map entry of "First Name" => "firstName"
	 * 
	 * @param beanClass
	 * @return A modifiable map.  Feel free to add or remove elements.
	 */
	public static Map<String, String> generateReverseHeaderMapFromProps(Class<?> beanClass) {
		LinkedHashMap<String, String> resultMap = new LinkedHashMap<String, String>();

		PropertyDescriptor[] pdArray = PropertyUtils.getPropertyDescriptors(beanClass);
		if (pdArray == null || pdArray.length == 0) {
			return resultMap;
		}

		List<PropertyDescriptor> writablePdList = Arrays.stream(pdArray)
				.filter(pd -> !pd.getName().equals("class"))
				.filter(pd -> pd.getWriteMethod() != null)
				.filter(pd -> (SepBasicTypeConverts.canTypeFromString(pd.getPropertyType()) || pd.getPropertyType().equals(java.util.Date.class)))
				.collect(Collectors.toList());
		List<String> finalProps = writablePdList.stream().map(pd -> pd.getName()).collect(Collectors.toList());

		for (String propName : finalProps) {
			String headerName = SepStringHelper.camelCaseToCapitalizedWords(propName);
			resultMap.put(headerName, propName);
		}

		return resultMap;
	}

}
