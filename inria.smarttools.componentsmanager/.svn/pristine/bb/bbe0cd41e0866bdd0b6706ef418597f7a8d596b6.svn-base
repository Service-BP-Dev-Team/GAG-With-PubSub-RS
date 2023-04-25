package inria.smarttools.componentsmanager;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

	@Override
	public String format(final LogRecord record) {
		final StringBuilder buffer = new StringBuilder();
		if (record.getLevel() == Level.WARNING) {
			buffer.append(" ! ");
		} else if (record.getLevel() == Level.SEVERE
				|| record.getThrown() != null) {
			buffer.append(" # ");
		} else {
			buffer.append(" * ");
		}
		buffer.append(new Date(record.getMillis()).toString());
		buffer.append(" [");
		buffer.append(record.getLoggerName());
		buffer.append("] ");
		buffer.append(record.getMessage());
		buffer.append("\n");
		if (record.getThrown() != null) {
			buffer.append(record.getThrown().getMessage());
			buffer.append("\n");
			for (int i = 0, j = record.getThrown().getStackTrace().length; i < j; i++) {
				buffer.append(record.getThrown().getStackTrace()[i]);
				buffer.append("\n");
			}
			if (record.getThrown().getCause() != null) {
				buffer.append(record.getThrown().getCause().getMessage());
				buffer.append("\n");
				for (int i = 0, j = record.getThrown().getCause()
						.getStackTrace().length; i < j; i++) {
					buffer
							.append(record.getThrown().getCause()
									.getStackTrace()[i]);
					buffer.append("\n");
				}
			}
		}
		return buffer.toString();
	}

}
