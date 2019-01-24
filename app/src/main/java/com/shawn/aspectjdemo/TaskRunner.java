package com.shawn.aspectjdemo;

import android.util.Log;

import com.shawn.aspectjdemo.aspect.Terminate;

/**
 * author: Shawn
 * time  : 1/24/19 11:50 AM
 * desc  :
 * update: Shawn 1/24/19 11:50 AM
 */
public class TaskRunner {

    private static final String TAG = "TaskRunner";
    private boolean terminate = false;

    public void run() {
        step1();
        step2();
        step3();
    }

    @Terminate
    private void step1() {
        if (terminate) return;
        Log.d(TAG, "step1: do it");
    }

    @Terminate
    private void step2() {
        if (terminate) return;
        Log.d(TAG, "step2: do it");
    }

    @Terminate
    private void step3() {
        if (terminate) return;
        Log.d(TAG, "step3: do it");
    }

    public boolean isTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
        Log.d(TAG, "setTerminate: " + terminate);
    }
}
