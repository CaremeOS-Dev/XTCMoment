package com.xtc.moment.monitor;

import com.xtc.log.LogUtil;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.GenericScheduledExecutorService;
import rx.internal.schedulers.ScheduledAction;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.MultipleAssignmentSubscription;
import rx.subscriptions.Subscriptions;

/**
 * 可排序的 RxJava Worker：任务先进入本地队列，再由底层线程池按优先级执行。
 */
public class ExecutorSchedulerWorker extends Scheduler.Worker
        implements IBaseWork, Comparable<IBaseWork>, Runnable {

    final Executor executor;
    protected String taskName;
    protected int priority;
    private boolean needCreateNewThread;

    final ConcurrentLinkedQueue<ScheduledAction> queue = new ConcurrentLinkedQueue<>();
    final AtomicInteger wip = new AtomicInteger();
    final CompositeSubscription tasks = new CompositeSubscription();

    public ExecutorSchedulerWorker(Executor executor, String taskName, int priority) {
        this.executor = executor;
        this.taskName = taskName;
        this.priority = priority;
    }

    @Override
    public Subscription schedule(Action0 action) {
        if (isUnsubscribed()) {
            return Subscriptions.unsubscribed();
        }
        ScheduledAction scheduledAction = new ScheduledAction(action, this.tasks);
        this.tasks.add(scheduledAction);
        this.queue.offer(scheduledAction);
        if (this.wip.getAndIncrement() == 0) {
            try {
                this.executor.execute(this);
            } catch (RejectedExecutionException e) {
                if (this.needCreateNewThread || getPriority() >= IOTaskPriorityType.DISCARD_TASK_VALUE) {
                    LogUtil.d(IOMonitorConstants.MONITOR_LOG_TAG,
                            "ExecutorSchedulerWorker execute this task again:" + this.taskName
                                    + "  needCreateNewThread:" + this.needCreateNewThread);
                    this.executor.execute(this);
                } else {
                    this.tasks.remove(scheduledAction);
                    this.wip.decrementAndGet();
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                    return throwErrorException();
                }
            }
        }
        return scheduledAction;
    }

    private Subscription throwErrorException() {
        LogUtil.d(IOMonitorConstants.MONITOR_LOG_TAG,
                ThreadPoolUtil.getSplitString(IOMonitorConstants.DISCARD_TASK_PREFIX, this.taskName));
        throw new UnsupportedOperationException(
                "this task is rejected, you need realize RxJava onError action! " + this.taskName);
    }

    @Override
    public void run() {
        do {
            ScheduledAction scheduledAction = this.queue.poll();
            if (!scheduledAction.isUnsubscribed()) {
                scheduledAction.run();
            }
        } while (this.wip.decrementAndGet() > 0);
    }

    @Override
    public Subscription schedule(final Action0 action, long delayTime, TimeUnit unit) {
        ScheduledExecutorService scheduledExecutorService;
        if (delayTime <= 0) {
            return schedule(action);
        }
        if (isUnsubscribed()) {
            return Subscriptions.unsubscribed();
        }
        if (this.executor instanceof ScheduledExecutorService) {
            scheduledExecutorService = (ScheduledExecutorService) this.executor;
        } else {
            scheduledExecutorService = GenericScheduledExecutorService.getInstance();
        }
        final MultipleAssignmentSubscription childSubscription = new MultipleAssignmentSubscription();
        MultipleAssignmentSubscription parentSubscription = new MultipleAssignmentSubscription();
        parentSubscription.set(childSubscription);
        this.tasks.add(parentSubscription);
        final Subscription cancelSubscription = Subscriptions.create(new Action0() {
            @Override
            public void call() {
                ExecutorSchedulerWorker.this.tasks.remove(parentSubscription);
            }
        });
        ScheduledAction scheduledAction = new ScheduledAction(new Action0() {
            @Override
            public void call() {
                if (childSubscription.isUnsubscribed()) {
                    return;
                }
                Subscription subscription = ExecutorSchedulerWorker.this.schedule(action);
                childSubscription.set(subscription);
                if (subscription.getClass() == ScheduledAction.class) {
                    ((ScheduledAction) subscription).add(cancelSubscription);
                }
            }
        });
        childSubscription.set(scheduledAction);
        try {
            scheduledAction.add(scheduledExecutorService.schedule(scheduledAction, delayTime, unit));
            return cancelSubscription;
        } catch (RejectedExecutionException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            throw e;
        }
    }

    @Override
    public boolean isUnsubscribed() {
        return this.tasks.isUnsubscribed();
    }

    @Override
    public void unsubscribe() {
        this.tasks.unsubscribe();
    }

    @Override
    public String getTaskName() {
        return this.taskName;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setNeedCreateNewThread(boolean needCreateNewThread) {
        this.needCreateNewThread = needCreateNewThread;
    }

    @Override
    public boolean isNeedCreateNewThread() {
        return this.needCreateNewThread;
    }

    @Override
    public int compareTo(IBaseWork other) {
        return this.priority >= other.getPriority() ? -1 : 1;
    }

    @Override
    public String toString() {
        return "{\"ExecutorSchedulerWorker\":{\"priority\":" + this.priority + ",\"taskName\":\"" + this.taskName
                + '"' + "}";
    }
}