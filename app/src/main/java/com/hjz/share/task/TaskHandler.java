package com.hjz.share.task;

import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

public class TaskHandler extends AsyncTask<Void, BaseTask, Void> {

    private static final String TAG = "TaskHandler-YH";
    private Queue<BaseTask> mQueue = new LinkedList<>();
    private BaseTask mActive;
    private int taskDone;
    private static  TaskHandler mTaskHandler;


    public static TaskHandler getTaskHandler() {
        if (mTaskHandler == null) {
            return mTaskHandler = new TaskHandler();
        }
        return mTaskHandler;
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "handler starting...");
    }

    @Override
    protected Void doInBackground(Void[] objects) {
        while (true) {
            synchronized (mQueue) {
                mActive = mQueue.poll();
                if (mActive != null) {
                    mActive.run();
                    Log.i(TAG, "mActive.run()");
                    publishProgress(mActive);
                }
                if (mQueue.size() == 0) {
                    try {
                        Log.i(TAG, "waiting for new task come in");
                        if (Thread.holdsLock(mQueue)) {
                            mQueue.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onProgressUpdate(BaseTask... tasks) {
        taskDone++;
        tasks[0].onPostExecute();
        Log.i(TAG, "task has done " + taskDone + " task");
    }

    public void post(BaseTask task) {
        Log.i(TAG, "new task come in");
        synchronized (mQueue) {
            Log.i(TAG, "new task offer queue");
            mQueue.offer(task);
            if (Thread.holdsLock(mQueue)) {
                Log.i(TAG, "new task offer queue and notifyAll");
                mQueue.notifyAll();
            }
        }
    }
}
