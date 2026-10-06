package com.xtc.database.ormlite;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/** Subscription helpers used by {@link RxDao}. */
public class RxUtil {

    /** Unsubscribes [subscription] when it is not null. */
    public static void unsubscribe(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    /** @return [subscription] when it is still active, otherwise a new one. */
    public static CompositeSubscription reset(CompositeSubscription subscription) {
        return (subscription == null || subscription.isUnsubscribed())
                ? new CompositeSubscription() : subscription;
    }
}