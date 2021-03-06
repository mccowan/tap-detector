package com.github.mccowan.common;

import java.io.PrintStream;

/**
 * TODO$(user): Class description
 *
 * @author com.github.mccowan
 */
public class Log {
    final static PrintStream STREAM = System.err;
    
    public static void d(final String tag, final Object... values) {
        emit("DEBUG", tag, values);
    }
    
    private static void emit(final String level, final String tag, final Object... values) {
        STREAM.print("[" + level + "] " + tag);
        for (Object value : values) {
            STREAM.print(value);
        }
        STREAM.println();
    }
}
