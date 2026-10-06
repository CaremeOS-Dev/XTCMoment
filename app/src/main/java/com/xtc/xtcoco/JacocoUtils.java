package com.xtc.xtcoco;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/** Writes the JaCoCo coverage dump when the test receiver asks for it. */
public class JacocoUtils {

    static String TAG = "JacocoUtils";

    private static volatile JacocoUtils singleton;

    private String baseline;
    private String gitSHA;

    private JacocoUtils() {
    }

    public static JacocoUtils getInstance() {
        if (singleton == null) {
            synchronized (JacocoUtils.class) {
                if (singleton == null) {
                    singleton = new JacocoUtils();
                }
            }
        }
        return singleton;
    }

    /** Records the build identifiers used in the dump file name. */
    public void init(String gitSHA, String baseline) {
        this.gitSHA = gitSHA;
        this.baseline = baseline;
    }

    /** Dumps the runtime coverage data to the external storage. */
    public void generateEcFile(Context context) throws Throwable {
        if (this.gitSHA == null || this.baseline == null || context == null) {
            return;
        }
        String path = getPath(context);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        FileOutputStream outputStream = null;
        try {
            Object agent = Class.forName("org.jacoco.agent.rt.RT").getMethod("getAgent").invoke(null);
            if (agent == null) {
                return;
            }
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file.getPath(), true);
            outputStream.write((byte[]) agent.getClass().getMethod("getExecutionData", Boolean.TYPE)
                    .invoke(agent, false));
            Log.d(TAG, "generate ec file success");
        } catch (Exception e) {
            Log.e(TAG, "generate ec file fail", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** @return the absolute path of the coverage dump file. */
    public String getPath(Context context) {
        if (context == null) {
            return "";
        }
        return Environment.getExternalStorageDirectory() + File.separator + "ec" + File.separator
                + context.getPackageName() + File.separator + this.gitSHA + "_" + this.baseline + File.separator
                + "coverage-" + System.currentTimeMillis() + ".exec";
    }
}