package by.bsu.up.chat.logging.impl;

import java.io.PrintStream;
import java.io.FileNotFoundException;
import by.bsu.up.chat.logging.Logger;

public class Log implements Logger {

    private static final String TEMPLATE = "[%s] %s";

    private PrintStream logWriter;

    private String tag;

    private Log(Class<?> cls, String path) throws FileNotFoundException {
        tag = String.format(TEMPLATE, cls.getName(), "%s");
        logWriter = new PrintStream(path);
    }
	
	private Log(Class<?> cls) {
        tag = String.format(TEMPLATE, cls.getName(), "%s");
        logWriter = System.out;
    }

    @Override
    public void info(String message) {
        logWriter.println(String.format(tag, message));
    }

    @Override
    public void error(String message, Throwable e) {
        logWriter.println(String.format(tag, message));
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
