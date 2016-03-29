package by.bsu.up.chat.logging.impl;

import java.io.PrintStream;
import java.io.FileNotFoundException;
import by.bsu.up.chat.logging.Logger;
import java.util.Calendar;

public class Log implements Logger {

    private static final String TEMPLATE = "[%s] : [%s] %s";

    private PrintStream logWriter;

    private String tag;

    private Log(Class<?> cls, String path) throws FileNotFoundException {
        tag = String.format(TEMPLATE, "%d", cls.getName(), "%s");
        logWriter = new PrintStream(path);
    }
	
	private Log(Class<?> cls) {
        tag = String.format(TEMPLATE, "%d", cls.getName(), "%s");
        logWriter = null;
    }

    @Override
    public void info(String message) {
        if(logWriter != null)
            logWriter.println(String.format(tag, Calendar.getInstance().getTime().getTime(), message));
        System.out.println(String.format(tag, Calendar.getInstance().getTime().getTime(), message));
    }

    @Override
    public void error(String message, Throwable e) {
        if(logWriter != null)
            logWriter.println(String.format(tag, Calendar.getInstance().getTime().getTime(), message));
        System.err.println(String.format(tag, Calendar.getInstance().getTime().getTime(), message));
        e.printStackTrace(System.err);
    }

    public static Log create(Class<?> cls, String path) {
		try {
			return new Log(cls, path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new Log(cls);
    }
	
	public static Log create(Class<?> cls) {
        return new Log(cls);
    }
}
