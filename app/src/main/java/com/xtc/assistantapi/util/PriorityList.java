package com.xtc.assistantapi.util;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * 按优先级排序的列表，优先级数值越大越靠前。
 */
public class PriorityList<T> extends AbstractList<T> {

    private final LinkedList<Node<T>> nodes;
    private final int defaultPriority;

    public PriorityList(int defaultPriority) {
        this.nodes = new LinkedList<>();
        this.defaultPriority = defaultPriority;
    }

    public PriorityList() {
        this(0);
    }

    /** 按指定优先级插入元素。 */
    public boolean add(T element, int priority) {
        Node<T> node = new Node<>(element, priority);
        if (this.nodes.isEmpty()) {
            this.nodes.add(node);
            return true;
        }
        ListIterator<Node<T>> iterator = this.nodes.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().priority < priority) {
                iterator.previous();
                iterator.add(node);
                return true;
            }
        }
        this.nodes.addLast(node);
        return true;
    }

    @Override
    @Deprecated
    public void add(int index, T element) {
        throw new UnsupportedOperationException("不支持添加到指定位置");
    }

    @Override
    public boolean add(T element) {
        return add(element, this.defaultPriority);
    }

    @Override
    public boolean remove(Object object) {
        Iterator<Node<T>> iterator = this.nodes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().element == object) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.nodes.size();
    }

    @Override
    public T get(int index) {
        return this.nodes.get(index).element;
    }

    /** 获取指定位置元素的优先级。 */
    public int getPriority(int index) {
        return this.nodes.get(index).priority;
    }

    @Override
    public T set(int index, T element) {
        Node<T> node = this.nodes.get(index);
        T previous = node.element;
        node.element = element;
        return previous;
    }

    @Override
    public Iterator<T> iterator() {
        return new NodeIterator(this);
    }

    private static class Node<T> {
        int priority;
        T element;

        Node(T element, int priority) {
            this.element = element;
            this.priority = priority;
        }
    }

    private class NodeIterator implements Iterator<T> {

        private final ListIterator<Node<T>> iterator;

        public NodeIterator(PriorityList<T> priorityList) {
            this(0);
        }

        public NodeIterator(int index) {
            this.iterator = PriorityList.this.nodes.listIterator(index);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next().element;
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}