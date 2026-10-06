package com.xtc.architecture.mvp.core;

/**
 * Marker for an MVP view.
 *
 * <p>Views are activities/fragments; the type parameter ties a view to the
 * presenter that drives it.
 */
public interface MvpView<P extends MvpPresenter> {
}
