package com.sinosoft.myapplication;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.List;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private android.os.Handler handler = new Handler(Looper.getMainLooper());
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    private Context mContext;
    private static CrashHandler instance;

    public static CrashHandler getInstance() {
        if (instance == null) {  //使用双重检查,好处在于后面线程不需要进入线程同步，直接判断instance提升效率
            synchronized (CrashHandler.class) {  //同步锁在此处
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;

    }

    private CrashHandler() {
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序发生崩溃，即将重启", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
//        Intent nextIntent = new Intent(mContext, MainActivity.class);
//        nextIntent.putExtra(EXTRA_TEXT, "Hello!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ProcessPhoenix.triggerRebirth(mContext);

//        SystemClock.sleep(2000);
//        final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        mContext.startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());


//        Intent intent = new Intent(mContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());

//        final PendingIntent pendingIntent = PendingIntent.getActivity(
//                mContext, 0, new Intent(mContext, MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
//        Log.d(mContext.getPackageName(), "Exception not handled, relaunching", throwable);
//        final AlarmManager alarmManager = (AlarmManager)mContext. getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
//        System.exit(0);
        mDefaultUncaughtExceptionHandler.uncaughtException(thread, throwable);

    }

    public void init(Context context) {
        /*
         * 弹出解决方案之后把崩溃继续交给系统处理，
         * 所以保存当前UncaughtExceptionHandler用于崩溃发生时使用。
         */
        if (context == null) {
            throw new IllegalArgumentException("Context is null!!!");
        }
        mContext = context.getApplicationContext();
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
}
