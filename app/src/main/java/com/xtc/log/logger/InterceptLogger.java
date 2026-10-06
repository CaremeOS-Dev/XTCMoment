package com.xtc.log.logger;

import android.content.Context;

import com.xtc.log.ILogger;
import com.xtc.log.LogConfig;
import com.xtc.log.LogUtil;
import com.xtc.log.algorithmUtil.ACAutomaton;
import com.xtc.log.exception.LogSecurityException;
import com.xtc.log.util.ProviderUtil;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Guards against logging sensitive data.
 *
 * <p>On a debug build running locally it loads the device sensitive-keyword
 * list into an Aho-Corasick automaton and checks every record against it; a hit
 * raises {@link LogSecurityException} to force the caller to mask the value.
 * Keyword loading is retried a bounded number of times.
 */
public class InterceptLogger implements ILogger {

    /** Maximum attempts to load the sensitive-keyword list. */
    private static final int COUNT = 5;

    private static final String TAG = "InterceptLogger";

    private ACAutomaton acAutomaton;
    public boolean isSensitiveListExist = false;
    private final LogConfig logConfig;
    private final ThreadPoolExecutor logExecutor;
    private Context mContext;
    private int resetCount;

    public InterceptLogger(LogConfig logConfig) {
        this.logConfig = logConfig;
        init();
        this.logExecutor = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    @Override
    public void flush() {
    }

    private void init() {
        LogConfig config = this.logConfig;
        if (config == null) {
            return;
        }
        this.mContext = config.context;
        if (config.isDebugVersion() && config.isLocal()) {
            this.resetCount = 0;
            initAC();
        }
    }

    @Override
    public void log(ILogger.Level level, String tag, String message, Throwable throwable) {
        if ((this.acAutomaton != null || this.resetCount > COUNT)
                && this.logConfig.isDebugVersion()
                && this.logConfig.isLocal()
                && this.isSensitiveListExist) {
            this.logExecutor.execute(new LogRunner(tag, message, this.acAutomaton));
        }
    }

    @Override
    public void close() {
        this.logExecutor.shutdown();
    }

    /** Scans a record for sensitive keywords off the logging thread. */
    private static class LogRunner implements Runnable {

        private ACAutomaton acAutomaton;
        private List<String> matches;
        private String tag;
        private final String message;

        public LogRunner(String tag, String message, ACAutomaton acAutomaton) {
            this.tag = tag;
            this.message = message;
            this.acAutomaton = acAutomaton;
        }

        @Override
        public void run() {
            ACAutomaton automaton = this.acAutomaton;
            if (automaton == null) {
                return;
            }
            this.matches = automaton.searchPatterns(this.message);
            List<String> found = this.matches;
            if (found == null || found.isEmpty()) {
                return;
            }
            LogUtil.w(InterceptLogger.TAG, "matched - " + this.matches.size() + this.tag);
            throw new LogSecurityException("日志包含敏感信息，请使用LogMask脱敏：" + this.tag + this.matches);
        }
    }

    /** Loads the keyword list, retrying while the provider returns nothing. */
    private void initAC() {
        Context context = this.mContext;
        if (context == null) {
            return;
        }
        if (this.resetCount > COUNT) {
            LogUtil.w(TAG, "initAC: count not build the ACTree");
            return;
        }
        try {
            String[] sensitiveList = ProviderUtil.getSensitiveList(context);
            if (sensitiveList != null) {
                this.acAutomaton = new ACAutomaton();
                this.acAutomaton.buildACAutomaton(sensitiveList);
                this.resetCount++;
                this.isSensitiveListExist = true;
            } else {
                this.resetCount++;
                initAC();
            }
        } catch (Exception e) {
            this.isSensitiveListExist = false;
            this.resetCount++;
            LogUtil.w(TAG, "initAC: " + e.getMessage());
        }
    }
}
