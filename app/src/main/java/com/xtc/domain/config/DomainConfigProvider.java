package com.xtc.domain.config;

/** Loads a domain config into the given cache. */
public interface DomainConfigProvider {
    boolean load(DomainConfigCache cache);
}