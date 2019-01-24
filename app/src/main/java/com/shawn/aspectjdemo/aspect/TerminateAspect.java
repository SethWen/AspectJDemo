package com.shawn.aspectjdemo.aspect;

import android.util.Log;

import com.shawn.aspectjdemo.MainActivity;
import com.shawn.aspectjdemo.TaskRunner;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * author: Shawn
 * time  : 1/24/19 10:48 AM
 * desc  :
 * update: Shawn 1/24/19 10:48 AM
 */
@Aspect
public class TerminateAspect {
    private static final String TAG = "TerminateAspect";

    @Pointcut("execution(@com.shawn.aspectjdemo.aspect.Terminate * *(..))")
    public void cut() {
    }

//    @Before("DebugToolMethod()")
//    public void onCutBefore(JoinPoint joinPoint) throws Throwable {
//        String key = joinPoint.getSignature().toString();
//        Log.d(TAG, "onCutBefore: " + key);
//    }

    @Around("cut()")
    public void onCutAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        TaskRunner runner = (TaskRunner) joinPoint.getThis();
        boolean terminate = runner.isTerminate();
        Log.d(TAG, "onDebugToolMethodAround: " + key + "---" + terminate);
        if (!terminate) {
            joinPoint.proceed();
        }
    }

//    @After("DebugToolMethod()")
//    public void onCutAfter(JoinPoint joinPoint) throws Throwable {
//        String key = joinPoint.getSignature().toString();
//        Log.d(TAG, "onCutAfter: " + key);
//    }
}
