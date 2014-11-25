package org.sep4j;

import java.text.MessageFormat;

/**
 * 
 * When this exception is thrown, it means some datum in the records is wrong
 * 
 * @author chenjianjx
 */
public class DatumException extends Exception {
	private static final long serialVersionUID = -6160610503938820467L;

	/**
	 * the datum is of the Nth record. 0-based
	 */
	private int recordIndex;

	/**
	 * the corresponding propName of this datum
	 */
	private String propName;

	public DatumException() {
		super();

	}

	public DatumException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

	public DatumException(String arg0) {
		super(arg0);

	}

	public DatumException(Throwable arg0) {
		super(arg0);

	}

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

	@Override
	public String toString() {
		return MessageFormat.format("recordIndex = {0}, propName = {1}", recordIndex, propName);
	}

}
