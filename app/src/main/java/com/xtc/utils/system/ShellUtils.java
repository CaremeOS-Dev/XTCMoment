package com.xtc.utils.system;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.List;

/** Runs shell commands and returns the combined stdout/stderr. */
public class ShellUtils {

    private ShellUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static CommandResult exec(String command, boolean isRoot) {
        return exec(new String[]{command}, isRoot, true);
    }

    public static CommandResult exec(List<String> commands, boolean isRoot) {
        return exec(commands == null ? null : commands.toArray(new String[0]), isRoot, true);
    }

    public static CommandResult exec(String[] commands, boolean isRoot) {
        return exec(commands, isRoot, true);
    }

    public static CommandResult exec(String command, boolean isRoot, boolean isNeedResultMsg) {
        return exec(new String[]{command}, isRoot, isNeedResultMsg);
    }

    public static CommandResult exec(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return exec(commands == null ? null : commands.toArray(new String[0]), isRoot, isNeedResultMsg);
    }

    public static CommandResult exec(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int resultCode = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(-1, null, null);
        }
        Process process = null;
        DataOutputStream outputStream = null;
        BufferedReader successReader = null;
        BufferedReader errorReader = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? "su" : "sh");
            outputStream = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                outputStream.write(command.getBytes());
                outputStream.writeBytes("\n");
                outputStream.flush();
            }
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            resultCode = process.waitFor();
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = successReader.readLine()) != null) {
                    successMsg.append(line);
                }
                while ((line = errorReader.readLine()) != null) {
                    errorMsg.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeWithLog(outputStream, successReader, errorReader);
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(resultCode, successMsg == null ? null : successMsg.toString(),
                errorMsg == null ? null : errorMsg.toString());
    }

    /** Result of {@link ShellUtils#exec}. */
    public static class CommandResult {

        /** Exit code of the shell process. */
        public int result;
        /** Captured stdout. */
        public String successMsg;
        /** Captured stderr. */
        public String errorMsg;

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }
}