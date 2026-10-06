package com.xtc.domain.config;

import com.xtc.domain.DomainConfig;

/** Receives a freshly loaded domain config. */
public interface DomainConfigCache {
    void update(DomainConfig domainConfig);
}