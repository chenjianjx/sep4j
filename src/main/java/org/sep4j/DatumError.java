package org.sep4j;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * 
 * some datum in the records is wrong e.g. its value or type does not match the
 * header
 * 
 * @author chenjianjx
 */
public class DatumError implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5587180904535809629L;

	/**
	 * the datum is of the Nth record. 0-based
	 */
	private int recordIndex;

	/**
	 * the corresponding propName of this datum
	 */
	private String propName;

	/**
	 * the cause of the error. It could be null
	 */
	private Exception cause;

	public int getRecordIndex() {
		return recordIndex;
	}

	public void setRecordIndex(int recordIndex) {
		this.recordIndex = recordIndex;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public Exception getCause() {
		return cause;
	}

	public void setCause(Exception cause) {
		this.cause = cause;
	}

	@Override
	public String toString() {
		return MessageFormat.format("recordIndex = {0}, propName = \"{1}\", cause = {2}", recordIndex, propName, cause);
	}

}
