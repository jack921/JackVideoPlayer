package com.example.administrator.jackvideoplayer.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.example.administrator.jackvideoplayer.R;

/**
 * Created by Administrator on 2017/11/8.
 */

public class DialogShow {

    private static PopupWindow scheduleWindow;
    private static PopupWindow brightnessWindow;
    private static PopupWindow soundWindow;

    private static View scheduleView;
    private static View brightnessView;
    private static View soundView;

    public static void loadingScheduleWindow(Context context){

        if(scheduleWindow==null&&scheduleView==null){
            scheduleView= LayoutInflater.from(context).inflate(R.layout.dialog_schedule_show,null);

        }else{

        }

    }

    public static void loadingBrightnessWindow(){

    }

    public static void loadingsoundWindow(){

    }

}
