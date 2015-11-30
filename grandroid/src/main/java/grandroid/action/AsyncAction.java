/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.action;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ProgressBar;
import grandroid.dialog.DialogMask;
import grandroid.dialog.GDialog;
import grandroid.dialog.GDialog.Builder;
import grandroid.view.LayoutMaker;

/**
 *
 * @author Rovers
 */
public abstract class AsyncAction<T> extends ContextAction {

    protected AsyncTask<Void, Void, T> task;
    protected T result;
    protected boolean multiThreadMode;
    protected boolean running;
    protected boolean cancelable;
    protected String message;
    protected LoadingBox loadingBox;

    public AsyncAction() {
        this(null, null);
    }

    public AsyncAction(Context context) {
        this(context, null);
    }

    public AsyncAction(final Context context, String actionName) {
        super(context, actionName);
        //dialogType = DIALOG_PROGRESS;
        message = "Loading...";
        if (context != null) {
            loadingBox = new DefaultLoadingBox();
        }
    }

    public void setContext(Context context) {
        this.context = context;
        loadingBox = new DefaultLoadingBox();
    }

    public AsyncAction cancelable() {
        cancelable = true;
        return this;
    }

    public AsyncAction customizeLoadingBox() {
        if (context != null) {
            loadingBox = new DialogBox();
        }
        return this;
    }

    public AsyncAction silence() {
        loadingBox = null;
        return this;
    }

    public AsyncAction message(String message) {
        this.message = message;
        return this;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void multiple() {
        this.multiThreadMode = true;
    }

    @Override
    public boolean execute() {
        if (!multiThreadMode && running) {
            return false;
        }
        if (!beforeExecution()) {
            return false;
        }
        running = true;

        task = new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... arg0) {
                AsyncAction.this.execute(context);
                return result;
            }

            @Override
            protected void onPreExecute() {
                result = null;
                if (loadingBox != null) {
                    loadingBox.show();
                }
            }

            @Override
            protected void onPostExecute(T result) {
                if (loadingBox != null) {
                    loadingBox.dismiss();
                }
                if (!isCancelled()) {
                    afterExecution(result);
                }
                running = false;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                running = false;
                if (loadingBox != null) {
                    loadingBox.dismiss();
                }
                onCanceled();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        } else {
            task.execute((Void[]) null);
        }
//        task.execute();
        return true;
    }

    public boolean interrupt() {
        return task.cancel(true);
    }

    public boolean beforeExecution() {
        return true;
    }

    public abstract void afterExecution(T result);

    public void onCanceled() {
        Log.e("grandroid", "AsyncAction is canceled");
    }

    public DialogMask createLoadingDialog(String message) {
        return new DialogMask(context) {
            @Override
            public boolean setupMask(Context context, Builder builder, LayoutMaker maker) throws Exception {
                maker.add(new ProgressBar(context));
                //builder.setCancelable(AsyncAction.this.cancelable);
                return true;
            }

            public boolean isCancelable() {
                return AsyncAction.this.cancelable;
            }

            @Override
            public void onCancel(DialogInterface dialogInterface) {
                task.cancel(true);
            }
        };
    }

    interface LoadingBox {

        public void show();

        public void dismiss();
    }

    class DefaultLoadingBox implements LoadingBox {

        ProgressDialog progress;

        public void show() {
            if (progress == null) {
                progress = new ProgressDialog(context);
            }
            if (AsyncAction.this.cancelable) {
                progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface arg0) {
                        task.cancel(true);
                    }
                });
            }
            progress.setCancelable(cancelable);
            progress.setMessage(message);
            progress.show();
        }

        public void dismiss() {
            progress.dismiss();
        }
    }

    class DialogBox implements LoadingBox {

        DialogMask dialog;

        public void show() {
            dialog = createLoadingDialog(message);
            dialog.show(GDialog.DialogStyle.Custom);
        }

        public void dismiss() {
            dialog.getDialog().dismiss();
        }
    }
}
