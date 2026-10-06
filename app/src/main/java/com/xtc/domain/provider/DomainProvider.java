package com.xtc.domain.provider;

/** Supplies the domain / environment configuration. */
public interface DomainProvider {
    String getDataCenterCode();

    String getDomain(String name);

    Integer getEnvId();

    String getEnvName();
}