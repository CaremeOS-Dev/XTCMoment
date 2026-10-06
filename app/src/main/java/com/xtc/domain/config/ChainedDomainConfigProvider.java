package com.xtc.domain.config;

import com.xtc.domain.DomainConfig;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/** Tries each provider in order, propagating updates back up the chain. */
public class ChainedDomainConfigProvider implements DomainConfigProvider {

    private final List<DomainConfigProvider> providers;

    public ChainedDomainConfigProvider(DomainConfigProvider... providers) {
        this.providers = Arrays.asList(providers);
    }

    @Override
    public boolean load(DomainConfigCache cache) {
        for (DomainConfigProvider provider : this.providers) {
            if (provider.load(new BreakChainDomainConfigCache(cache, provider))) {
                return true;
            }
        }
        return false;
    }

    private class BreakChainDomainConfigCache implements DomainConfigCache {
        private final DomainConfigCache delegate;
        private final DomainConfigProvider source;

        public BreakChainDomainConfigCache(DomainConfigCache delegate, DomainConfigProvider source) {
            this.delegate = delegate;
            this.source = source;
        }

        @Override
        public void update(DomainConfig domainConfig) {
            this.delegate.update(domainConfig);
            Iterator<DomainConfigProvider> iterator = ChainedDomainConfigProvider.this.providers.iterator();
            while (iterator.hasNext()) {
                DomainConfigProvider provider = iterator.next();
                if (provider == this.source) {
                    break;
                }
                if (provider instanceof DomainConfigCache) {
                    ((DomainConfigCache) provider).update(domainConfig);
                }
            }
        }
    }
}