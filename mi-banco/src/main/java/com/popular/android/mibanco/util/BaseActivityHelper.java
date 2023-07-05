package com.popular.android.mibanco.util;

import android.app.Dialog;

import com.popular.android.mibanco.task.BaseAsyncTask;

import java.util.Stack;

/** Holds extra fields and methods common for all base Activity classes. */
public class BaseActivityHelper {

    /** The stack of AsyncTasks to cancel on Activity's destroy. */
    private Stack<BaseAsyncTask> tasksToCancel = new Stack<BaseAsyncTask>();

    /** The stack of Dialogs to dismiss on Activity's destroy. */
    private Stack<Dialog> dialogsToDismiss = new Stack<Dialog>();

    /**
     * Adds a task to the stack of tasks to cancel on Activity's destroy.
     * 
     * @param task the task to add to the stack
     */
    public void addTaskToCancelOnDestroy(BaseAsyncTask task) {
        tasksToCancel.push(task);
    }

    /**
     * Adds a dialog to the stack of dialogs to dismiss on Activity's destroy.
     * 
     * @param dialog the dialog to add to the stack
     */
    public void addDialogToDismissOnDestroy(Dialog dialog) {
        dialogsToDismiss.push(dialog);
    }

    /** Cancels all tasks pushed onto the stack. */
    public void cancelStackedTasks() {
        for (BaseAsyncTask taskToCancel : tasksToCancel) {
            if (taskToCancel != null) {
                taskToCancel.cancel(true);
            }
        }
    }

    /** Dismisses all dialogs pushed onto the stack. */
    public void dismissStackedDialogs() {
        for (Dialog dialog : dialogsToDismiss) {
            if (dialog != null) {
                Utils.dismissDialog(dialog);
            }
        }
    }
}
