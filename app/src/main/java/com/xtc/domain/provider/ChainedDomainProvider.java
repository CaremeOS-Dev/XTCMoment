package com.xtc.domain.provider;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/** Queries each provider until one returns a value. */
public class ChainedDomainProvider implements DomainProvider {

    private final List<DomainProvider> providers;

    public ChainedDomainProvider(DomainProvider... providers) {
        this.providers = Arrays.asList(providers);
    }

    @Override
    public synchronized String getDataCenterCode() {
        Iterator<DomainProvider> iterator = this.providers.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next().getDataCenterCode();
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public synchronized Integer getEnvId() {
        Iterator<DomainProvider> iterator = this.providers.iterator();
        while (iterator.hasNext()) {
            Integer value = iterator.next().getEnvId();
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public synchronized String getEnvName() {
        Iterator<DomainProvider> iterator = this.providers.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next().getEnvName();
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public synchronized String getDomain(String name) {
        Iterator<DomainProvider> iterator = this.providers.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next().getDomain(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}