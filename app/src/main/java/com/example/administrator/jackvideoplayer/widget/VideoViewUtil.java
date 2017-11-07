package com.example.administrator.jackvideoplayer.widget;

/**
 * Created by Administrator on 2017/11/7.
 */

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Administrator on 2017/11/1.
 */

public class VideoViewUtil {

    public static String formatTime(long milliseconds){
        if(milliseconds<=0||milliseconds>=24*60*60*1000){
            return "00:00";
        }
        long totalSeconds = milliseconds / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static void showToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static Activity scanForActivity(Context context){
        if(context ==null){
            return null;
        }
        if(context instanceof Activity){
            return (Activity)context;
        }else if(context instanceof ContextWrapper){
            return scanForActivity(((ContextWrapper)context).getBaseContext());
        }
        return null;
    }

    public static void hideActionBar(Context context){
        ActionBar ab=getAppCompActivity(context).getSupportActionBar();
        if(ab!=null){
            ab.setShowHideAnimationEnabled(false);
            ab.hide();
        }
        scanForActivity(context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public static void showActionBar(Context context){
        ActionBar ab=getAppCompActivity(context).getSupportActionBar();
        if(ab!=null){
            ab.setShowHideAnimationEnabled(false);
            ab.show();
        }
        scanForActivity(context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }


}
