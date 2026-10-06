package com.xtc.utils.system;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Immutable set of fun-list feature codes.
 *
 * <p>Only {@link #contains(Object)} and {@link #toString()} are meaningful; the
 * remaining {@link Set} methods throw because the original library never used
 * them.
 */
public class FunList implements Set<Integer> {

    private final Set<Integer> delegate;

    /** Builds a fun list, rejecting duplicate codes. */
    public static FunList of(Integer... codes) {
        HashSet<Integer> set = new HashSet<>();
        for (Integer code : codes) {
            if (!set.add(code)) {
                throw new IllegalArgumentException("Duplicated fun=" + code);
            }
        }
        return new FunList(Collections.unmodifiableSet(set));
    }

    private FunList(Set<Integer> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public boolean contains(Object object) {
        return this.delegate.contains(object);
    }

    @Override
    public Iterator<Integer> iterator() {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public <T> T[] toArray(T[] array) {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public boolean add(Integer value) {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public boolean addAll(Collection<? extends Integer> collection) {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("You not need this.");
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }
}