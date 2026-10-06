package com.xtc.dispatch.sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/** Topologically sorts the tasks so that every dependency runs before its dependents. */
public class DependSortUtil {

    /**
     * Sorts [tasks] by their {@link IDepend#dependsOn()} declarations.
     *
     * @param taskTypes the declared task types, used to resolve a dependency by class
     */
    public static synchronized <P extends IDepend> List<P> sort(List<P> tasks, List<Class<? extends IDepend>> taskTypes) {
        TreeSet<Integer> dependentIndexes = new TreeSet<>();
        Graph graph = new Graph(tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            P task = tasks.get(i);
            if (task.dependsOn() == null || task.dependsOn().size() == 0) {
                continue;
            }
            for (Class<? extends IDepend> dependency : task.dependsOn()) {
                int index = indexOf(tasks, taskTypes, dependency);
                if (index < 0) {
                    throw new IllegalStateException(task.getClass().getSimpleName() + " depends on "
                            + dependency.getSimpleName() + " can not be found in task list ");
                }
                dependentIndexes.add(index);
                graph.addEdge(index, i);
            }
        }
        return reorder(tasks, dependentIndexes, graph.topologicalSort());
    }

    private static <P extends IDepend> List<P> reorder(List<P> tasks, Set<Integer> dependentIndexes,
            List<Integer> order) {
        ArrayList<P> result = new ArrayList<>(tasks.size());
        ArrayList<P> dependent = new ArrayList<>();
        ArrayList<P> independent = new ArrayList<>();
        Iterator<Integer> iterator = order.iterator();
        while (iterator.hasNext()) {
            int index = iterator.next();
            if (dependentIndexes.contains(index)) {
                dependent.add(tasks.get(index));
            } else {
                independent.add(tasks.get(index));
            }
        }
        result.addAll(dependent);
        result.addAll(independent);
        return result;
    }

    private static <P extends IDepend> int indexOf(List<P> tasks, List<Class<? extends IDepend>> taskTypes,
            Class<? extends IDepend> dependency) {
        int index = taskTypes.indexOf(dependency);
        if (index >= 0) {
            return index;
        }
        int size = tasks.size();
        for (int i = 0; i < size; i++) {
            if (dependency.getSimpleName().equals(tasks.get(i).getClass().getSimpleName())) {
                return i;
            }
        }
        return index;
    }

    /** Adjacency list of the dependency graph. */
    public static class Graph {

        private final int vertexCount;
        private final List<Integer>[] edges;

        Graph(int vertexCount) {
            this.vertexCount = vertexCount;
            this.edges = new ArrayList[this.vertexCount];
            for (int i = 0; i < this.vertexCount; i++) {
                this.edges[i] = new ArrayList<>();
            }
        }

        void addEdge(int from, int to) {
            this.edges[from].add(to);
        }

        /** @return the vertices in topological order. */
        Vector<Integer> topologicalSort() {
            int[] inDegree = new int[this.vertexCount];
            for (int i = 0; i < this.vertexCount; i++) {
                Iterator<Integer> iterator = this.edges[i].iterator();
                while (iterator.hasNext()) {
                    int to = iterator.next();
                    inDegree[to] = inDegree[to] + 1;
                }
            }
            LinkedList<Integer> queue = new LinkedList<>();
            for (int i = 0; i < this.vertexCount; i++) {
                if (inDegree[i] == 0) {
                    queue.add(i);
                }
            }
            Vector<Integer> sorted = new Vector<>();
            int visited = 0;
            while (!queue.isEmpty()) {
                int current = queue.poll();
                sorted.add(current);
                Iterator<Integer> iterator = this.edges[current].iterator();
                while (iterator.hasNext()) {
                    int next = iterator.next();
                    int remaining = inDegree[next] - 1;
                    inDegree[next] = remaining;
                    if (remaining == 0) {
                        queue.add(next);
                    }
                }
                visited++;
            }
            if (visited == this.vertexCount) {
                return sorted;
            }
            throw new IllegalStateException("Exists a cycle in the graph");
        }
    }
}