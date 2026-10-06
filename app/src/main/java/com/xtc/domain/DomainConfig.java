package com.xtc.domain;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.xtc.httplib.ConfigOptions;
import com.xtc.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/** Remote domain configuration for one data centre. */
public class DomainConfig {

    private static final String TAG = "DomainConfig";
    private static final String KEY_THIRD_PARTY_DOMAIN = "thirdPartyDomain";
    private static final String KEY_IM_JOIN_DOMAIN = "imJoinDomain";
    private static final String KEY_IM_BACKUP_DOMAIN = "imBackupDomain";
    private static final String KEY_IM_JOIN_GREY_DOMAIN = "imJoinGreyDomain";
    private static final String KEY_IM_GREY_BACKUP_DOMAIN = "imGreyBackupDomain";

    private static final Gson GSON = new Gson();

    @SerializedName(ConfigOptions.HeaderKey.DATA_CENTER)
    private final String dataCenterCode;

    @SerializedName("domainId")
    private final Integer domainId;

    @SerializedName("name")
    private final String name;

    @SerializedName("appDomain")
    private final Map<String, String> appDomain;

    @SerializedName("imDomain")
    private final Map<String, String> imDomain;

    public static DomainConfig fromStream(InputStream inputStream) {
        try {
            BufferedSource source = Okio.buffer(Okio.source(inputStream));
            try {
                DomainConfig config = (DomainConfig) GSON.fromJson(source.readString(StandardCharsets.UTF_8), DomainConfig.class);
                if (config == null) {
                    source.close();
                    return null;
                }
                Map<String, String> appDomains = config.appDomain != null ? config.appDomain : Collections.<String, String>emptyMap();
                Map<String, String> imDomains = config.imDomain != null ? config.imDomain : Collections.<String, String>emptyMap();
                HashMap<String, String> merged = new HashMap<>(config.appDomain);
                String thirdParty = appDomains.get(KEY_THIRD_PARTY_DOMAIN);
                if (!TextUtils.isEmpty(thirdParty)) {
                    merged.put(Domain.THIRD.getName(), thirdParty);
                }
                String imJoin = imDomains.get(KEY_IM_JOIN_DOMAIN);
                if (!TextUtils.isEmpty(imJoin)) {
                    merged.put(Domain.IM_JOIN.getName(), imJoin);
                }
                String imBackup = imDomains.get(KEY_IM_BACKUP_DOMAIN);
                if (!TextUtils.isEmpty(imJoin)) {
                    String[] backupDomains = (String[]) GSON.fromJson(imBackup, String[].class);
                    if (backupDomains != null) {
                        merged.put(Domain.IM_BACKUP.getName(), TextUtils.join(",", backupDomains));
                    }
                }
                String imJoinGrey = imDomains.get(KEY_IM_JOIN_GREY_DOMAIN);
                if (!TextUtils.isEmpty(imJoin)) {
                    merged.put(Domain.IM_JOIN_GREY.getName(), imJoinGrey);
                }
                DomainConfig result = new DomainConfig(config.getDataCenterCode(), config.getDomainId(),
                        config.getName(), merged);
                source.close();
                return result;
            } catch (Throwable t) {
                source.close();
                throw t;
            }
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static DomainConfig fromFile(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            try {
                DomainConfig config = fromStream(inputStream);
                inputStream.close();
                return config;
            } catch (Throwable t) {
                inputStream.close();
                throw t;
            }
        } catch (IOException ignored) {
            return null;
        }
    }

    /** Writes the config into {@code file}. */
    public static boolean writeToFile(File file, DomainConfig config) {
        DomainConfig flattened = config.flatten();
        File parent = file.getParentFile();
        if (parent.isFile()) {
            LogUtil.i(TAG, String.format(Locale.ROOT, "%s is not dir, delete: %b", file.getPath(), Boolean.valueOf(parent.delete())));
        }
        LogUtil.i(TAG, "mkdirs: " + parent.mkdirs());
        String json = GSON.toJson(flattened);
        try {
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            try {
                sink.writeString(json, StandardCharsets.UTF_8);
                sink.close();
                return true;
            } catch (Throwable t) {
                sink.close();
                throw t;
            }
        } catch (IOException ignored) {
            return false;
        }
    }

    private DomainConfig flatten() {
        Map<String, String> appDomains = this.appDomain != null ? this.appDomain : Collections.<String, String>emptyMap();
        HashMap<String, String> merged = new HashMap<>(appDomains);
        merged.put(KEY_THIRD_PARTY_DOMAIN, appDomains.get(Domain.THIRD.getName()));
        HashMap<String, String> imDomains = new HashMap<>();
        if (appDomains.get(Domain.IM_JOIN.getName()) != null) {
            imDomains.put(KEY_IM_JOIN_DOMAIN, appDomains.get(Domain.IM_JOIN.getName()));
        }
        if (appDomains.get(Domain.IM_JOIN_GREY.getName()) != null) {
            imDomains.put(KEY_IM_JOIN_GREY_DOMAIN, appDomains.get(Domain.IM_JOIN_GREY.getName()));
        }
        if (appDomains.get(Domain.IM_BACKUP.getName()) != null) {
            imDomains.put(KEY_IM_BACKUP_DOMAIN, GSON.toJson(appDomains.get(Domain.IM_BACKUP.getName()).split(",")));
        }
        return new DomainConfig(this.dataCenterCode, this.domainId, this.name, merged, imDomains);
    }

    public DomainConfig(String dataCenterCode, Integer domainId, String name, Map<String, String> appDomain) {
        this.dataCenterCode = dataCenterCode;
        this.domainId = domainId;
        this.name = name;
        this.appDomain = appDomain;
        this.imDomain = new HashMap<>();
    }

    private DomainConfig(String dataCenterCode, Integer domainId, String name, Map<String, String> appDomain,
                         Map<String, String> imDomain) {
        this.dataCenterCode = dataCenterCode;
        this.domainId = domainId;
        this.name = name;
        this.appDomain = appDomain;
        this.imDomain = imDomain;
    }

    public String getDataCenterCode() {
        return this.dataCenterCode;
    }

    public Integer getDomainId() {
        return this.domainId;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getAppDomain() {
        return this.appDomain;
    }

    public Map<String, String> getImDomain() {
        return this.imDomain;
    }

    @Override
    public String toString() {
        return "DomainConfig{dataCenterCode=\'" + this.dataCenterCode + "\', envId=" + this.domainId + ", envName=\'"
                + this.name + "\', domains=" + this.appDomain + ", imDomain=" + this.imDomain + '}';
    }
}