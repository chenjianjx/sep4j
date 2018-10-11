package org.sep4j;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

@SuppressWarnings("unused")
public class HeaderUtilsTestRecord {
	
	private int primIntProp;
	private Integer intObjProp;
	private String strProp;
	private Date dateProp;
	private String readOnlyProp;
	private String writeOnlyProp;

	public int getPrimIntProp() {
		return primIntProp;
	}

	public void setPrimIntProp(int primIntProp) {
		this.primIntProp = primIntProp;
	}

	public Integer getIntObjProp() {
		return intObjProp;
	}

	public void setIntObjProp(Integer intObjProp) {
		this.intObjProp = intObjProp;
	}

	public String getStrProp() {
		return strProp;
	}

	public void setStrProp(String strProp) {
		this.strProp = strProp;
	}

	public Date getDateProp() {
		return dateProp;
	}

	public void setDateProp(Date date) {
		this.dateProp = date;
	}

	public String getDatePropStr() {
		if (dateProp == null) {
			return null;
		}
		return DateFormatUtils.format(dateProp, "yyyy-MM-dd HH:mm:ss");
	}

	public void setDateProp(String s) {
		if (s == null) {
			return;
		}
		try {
			Date d = DateUtils.parseDate(s, new String[] { "yyyy-MM-dd HH:mm:ss" });
			this.setDateProp(d);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}

	}

	public String getReadOnlyProp() {
		return readOnlyProp;
	}

	public void setWriteOnlyProp(String writeOnlyProp) {
		this.writeOnlyProp = writeOnlyProp;
	}

}