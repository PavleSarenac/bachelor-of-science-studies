package rs.ac.bg.etf.pp1.util;

import java.io.File;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

public class Log4JUtils {

    private static final Log4JUtils logs = new Log4JUtils();

    public static Log4JUtils instance() {
        return logs;
    }

    public void prepareLogFile(Logger root) {
        Appender appender = root.getAppender("file");

        if (!(appender instanceof FileAppender))
            return;
        FileAppender fAppender = (FileAppender) appender;

        String originalLogFileName = fAppender.getFile();
        String logFileName = originalLogFileName.substring(0, originalLogFileName.lastIndexOf('.')) + "_run.log";

        File logFile = new File(logFileName);
        File renamedFile = new File(logFile.getAbsoluteFile() + "." + System.currentTimeMillis());

        if (logFile.exists()) {
            if (!logFile.renameTo(renamedFile))
                System.err.println("Could not rename log file!");
        }

        fAppender.setFile(logFile.getAbsolutePath());
        fAppender.activateOptions();
    }
}
