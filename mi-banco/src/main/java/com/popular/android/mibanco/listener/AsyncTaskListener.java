package com.popular.android.mibanco.listener;

/**
 * Interface class to implement listener for async tasks
 * @param <T>
 */
public interface AsyncTaskListener<T> extends TaskListener {

    void onSuccess(T result);

    /**
     * Called on failed task.
     * 
     * @param error The Throwable, which caused a task to fail. May be null.
     * @return true to block a standard error dialog
     */
    boolean onError(Throwable error);

    void onCancelled();
}
