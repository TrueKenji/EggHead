package logger;

import java.util.Arrays;

public class ConsoleLogger extends Logger {
    
    @Override
    public void println(Object o, Level... levels) {
        if (isLog(levels)) {
            println(o.toString(), levels);
        }
    }
    
    @Override
    public void println(String s, Level... levels) {
        if (isLog(levels)) {
            System.out.println(getFormattedLine(s, levels));
        }
    }
    
    public String getFormattedLine(String s, Level... levels) {
        String res = "";
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = Arrays.stream(stackTraceElements).filter(e -> !e.getClassName().contains("logger") && !e.getClassName().contains("Thread")).findFirst().orElse(null);
        if (caller != null) {
            res += caller.getClassName() + "." + caller.getMethodName() + " l." + caller.getLineNumber();
        }
        return res + " : " + s + " | " + Arrays.toString(levels);
    }
}
