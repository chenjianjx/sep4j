package org.sep4j.support;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.sep4j.InvalidHeaderRowException;

/**
 * A function that are allowed to throw some parsing-related exceptions
 * Created by chenjianjx@gmail.com on 14/10/18.
 */
public interface FunctionThrowingParseException<T, R> {
    R applyThrows(T t) throws InvalidFormatException, InvalidHeaderRowException;
}
