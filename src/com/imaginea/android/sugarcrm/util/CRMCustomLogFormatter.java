package com.imaginea.android.sugarcrm.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * CRMCustomLogFormatter formats the LogRecord as follows: date level localized message with
 * parameters
 */
public class CRMCustomLogFormatter extends Formatter {

    DateFormat mDateFormat;

    /**
     * <p>Constructor for CRMCustomLogFormatter.</p>
     */
    public CRMCustomLogFormatter() {
        super();
        mDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    /** {@inheritDoc} */
    public String format(LogRecord record) {

        // Create a StringBuffer to contain the formatted record
        // start with the date.
        StringBuffer sb = new StringBuffer();

        // Get the date from the LogRecord and add it to the buffer
        Date date = new Date(record.getMillis());
        sb.append(mDateFormat.format(date.getTime()));
        sb.append(" ");

        // Get the level name and add it to the buffer
        sb.append(record.getLevel().getName());
        sb.append(" ");

        // Get the formatted message (includes localization
        // and substitution of paramters) and add it to the buffer
        sb.append(formatMessage(record));
        sb.append("\n");

        return sb.toString();
    }
}
