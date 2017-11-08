package com.example.administrator.jackvideoplayer.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.jackvideoplayer.R;

/**
 * Created by Administrator on 2017/11/8.
 */

public class DialogShow {

    private static PopupWindow scheduleWindow;
    private static PopupWindow brightnessWindow;
    private static PopupWindow soundWindow;

    private static View scheduleView;
    private static TextView schedultTime;
    private static View brightnessView;
    private static View soundView;

    public static void loadingScheduleWindow(Context context,String time){
        if(scheduleWindow==null){
            scheduleView= LayoutInflater.from(context).inflate(R.layout.dialog_schedule_show,null);
            scheduleWindow=new PopupWindow(scheduleView,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            scheduleWindow.setFocusable(true);
            scheduleWindow.setOutsideTouchable(true);
            scheduleWindow.setTouchable(true);
            scheduleWindow.showAtLocation(scheduleView, Gravity.CENTER,0,0);
        }else{
            ((TextView)scheduleView.findViewById(R.id.progressbar_time)).setText(time);
        }
    }

    public static void closeScheduleWindow(){
        if(scheduleWindow!=null){
            scheduleWindow.dismiss();
            scheduleWindow=null;
        }
    }

    public static void loadingBrightnessWindow(){

    }

    public static void loadingsoundWindow(){

    }

}
