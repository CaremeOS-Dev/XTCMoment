package com.xtc.log;

import android.content.Context;

import com.xtc.log.crash.CrashHandler;
import com.xtc.log.logger.AndroidLogger;
import com.xtc.log.logger.AsyncLogger;
import com.xtc.log.logger.FilterLogger;
import com.xtc.log.logger.InterceptLogger;
import com.xtc.log.logger.StandardOutLogger;

/**
 * Configuration for the logging stack.
 *
 * <p>Build one with {@link #builder()} and install it through a
 * {@link Builder.BuildMode}: {@code Android} wires logcat + file logging and
 * installs the crash handler, while {@code Java} wires standard out for
 * off-device runs.
 */
public class LogConfig {

    private String appName;
    public Context context;
    private String filePrefix;
    private boolean isDebugVersion;
    private boolean isLocal;
    private boolean isPrintConsole;
    private boolean isSaveLog;
    private String logPath;
    private String module;
    private String netPropertiesPath;

    /** Sentinel meaning "no explicit logging switch has been chosen". */
    public int sLogEnabled;

    private ILogger.Level saveLevel;

    private LogConfig(boolean isSaveLog, boolean isPrintConsole, String logPath, String module,
                      String appName, String filePrefix, ILogger.Level saveLevel,
                      String netPropertiesPath, boolean isDebugVersion, Context context, boolean isLocal) {
        this.sLogEnabled = -1;
        this.isSaveLog = isSaveLog;
        this.isPrintConsole = isPrintConsole;
        this.logPath = logPath;
        this.module = module;
        this.appName = appName;
        this.filePrefix = filePrefix;
        this.saveLevel = saveLevel;
        this.netPropertiesPath = netPropertiesPath;
        this.isDebugVersion = isDebugVersion;
        this.context = context;
        this.isLocal = isLocal;
    }

    public String getNetConfigPath() {
        return this.netPropertiesPath;
    }

    public String getFilePrefix() {
        return this.filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public boolean isSaveLog() {
        return this.isSaveLog;
    }

    public void setSaveLog(boolean saveLog) {
        this.isSaveLog = saveLog;
    }

    public boolean isPrintConsole() {
        return this.isPrintConsole;
    }

    void setPrintConsole(boolean printConsole) {
        this.isPrintConsole = printConsole;
    }

    public String getLogPath() {
        return this.logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getModule() {
        return this.module;
    }

    public String getAppName() {
        return this.appName;
    }

    public ILogger.Level getSaveLevel() {
        return this.saveLevel;
    }

    public void setSaveLevel(ILogger.Level saveLevel) {
        this.saveLevel = saveLevel;
    }

    public boolean isDebugVersion() {
        return this.isDebugVersion;
    }

    public boolean isLocal() {
        return this.isLocal;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Fluent builder; call {@link #build(Builder.BuildMode, Object...)} to install. */
    public static class Builder {

        private Context context;
        private String netConfigPath;
        private boolean isSaveLog = true;
        private boolean isPrintConsole = false;
        private boolean isDebugVersion = false;
        private boolean isLocal = false;
        private String logPath = "";
        private String module = "";
        private String appName = "";
        private String filePrefix = "";
        private ILogger.Level saveLevel = ILogger.Level.Debug;

        /** How the logging stack is installed. */
        public enum BuildMode {
            /** Logcat + file logging; also installs the crash handler. */
            Android {
                @Override
                public void build(LogConfig logConfig, Object... args) {
                    if (logConfig.isSaveLog()) {
                        logConfig.setPrintConsole(true);
                    }
                    Log.setLogger(new FilterLogger(
                            new AsyncLogger(
                                    new AndroidLogger(new InterceptLogger(logConfig), logConfig)),
                            logConfig));
                    CrashHandler.Instance.init();
                }
            },
            /** Standard out only, for off-device runs. */
            Java {
                @Override
                public void build(LogConfig logConfig, Object... args) {
                    Log.setLogger(StandardOutLogger.Instance);
                }
            };

            public abstract void build(LogConfig logConfig, Object... args);
        }

        Builder() {
        }

        public Builder saveLog(boolean saveLog) {
            this.isSaveLog = saveLog;
            return this;
        }

        public Builder isPrintConsole(boolean isPrintConsole) {
            this.isPrintConsole = isPrintConsole;
            return this;
        }

        public Builder netConfigPath(String netConfigPath) {
            this.netConfigPath = netConfigPath;
            return this;
        }

        public Builder logPath(String logPath) {
            this.logPath = logPath;
            return this;
        }

        public Builder module(String module) {
            this.module = module;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder isDebugVersion(boolean isDebugVersion) {
            this.isDebugVersion = isDebugVersion;
            return this;
        }

        public Builder isLocal(boolean isLocal) {
            this.isLocal = isLocal;
            return this;
        }

        public Builder currentContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setSaveLevel(ILogger.Level saveLevel) {
            this.saveLevel = saveLevel;
            return this;
        }

        public void build(BuildMode buildMode, Object... args) {
            buildMode.build(newInstance(), args);
        }

        LogConfig newInstance() {
            return new LogConfig(
                    this.isSaveLog,
                    this.isPrintConsole,
                    this.logPath,
                    this.module,
                    this.appName,
                    this.filePrefix.equals("") ? this.appName : this.filePrefix,
                    this.saveLevel,
                    this.netConfigPath,
                    this.isDebugVersion,
                    this.context,
                    this.isLocal);
        }
    }
}
