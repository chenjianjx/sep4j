package org.sep4j;

import java.text.MessageFormat;

/**
 * 
 * When this exception is thrown, it means some cell in the sheet is wrong
 * 
 * @author chenjianjx
 */
public class CellException extends Exception {
	private static final long serialVersionUID = -6160610503938820467L;

	/**
	 * the cell's rowIndex. 0-based
	 */
	private int rowIndex;

	/**
	 * the cell's columnIndex. 0-based
	 */
	private int columnIndex;

	/**
	 * the corresponding propName
	 */
	private String propName;

	public CellException() {
		super();

	}

	public CellException(String message, Throwable cause) {
		super(message, cause);

	}

	public CellException(String message) {
		super(message);

	}

	public CellException(Throwable cause) {
		super(cause);

	}

	@Override
	public String toString() {
		return MessageFormat.format("rowIndex = {0}, columnIndex = {1}, propName = {2}", rowIndex, columnIndex, propName);
	}

}
