/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class that executed cmd line commands.
 * 
 * @author Dominik Fuchss
 */
public final class ExecuteCmd {
    private static final Logger logger = LoggerFactory.getLogger(ExecuteCmd.class);
    private static final int DEFAULT_WAIT_IN_SECONDS = 15;

    private ExecuteCmd() {
        throw new IllegalAccessError();
    }

    /**
     * Run a command on the command line.
     * 
     * @param command the command
     * @return the result of the execution
     */
    public static ExecuteResult runCommand(String command) {
        return runCommand(command, DEFAULT_WAIT_IN_SECONDS);
    }

    /**
     * Run a command on the command line.
     * 
     * @param command                  the command
     * @param waitForResponseInSeconds the time to wait until canceling
     * @return the result of the execution
     */
    @SuppressWarnings("java:S2142")
    public static ExecuteResult runCommand(String command, int waitForResponseInSeconds) {
        try {
            Process process = Runtime.getRuntime().exec(command);

            String stdOut = readStream(process.getInputStream());
            String stdErr = readStream(process.getErrorStream());

            boolean exited = process.waitFor(waitForResponseInSeconds, TimeUnit.SECONDS);
            if (!exited) {
                logger.error("Timout in #runCommand(String, int)");
                return new ExecuteResult(Integer.MIN_VALUE, null, "Timeout! Cancelling request!");
            }
            return new ExecuteResult(process.exitValue(), stdOut, stdErr);
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
            return new ExecuteResult(Integer.MIN_VALUE, null, e.getMessage());
        }
    }

    private static String readStream(InputStream data) {
        if (data == null)
            return null;

        try (var scanner = new Scanner(data, StandardCharsets.UTF_8).useDelimiter("\\A")) {
            if (!scanner.hasNext())
                return null;
            return scanner.next();
        }
    }

    /**
     * The result of an execution of a command.
     * 
     * @param exitCode the exit code of the process (will be set to {@link Integer#MIN_VALUE} on timeout)
     * @param stdOut   the content from the output stream
     * @param stdErr   the content from the error stream
     * @author Dominik Fuchss
     */
    public record ExecuteResult(int exitCode, String stdOut, String stdErr) {
        public boolean success() {
            return exitCode == 0;
        }
    }
}
