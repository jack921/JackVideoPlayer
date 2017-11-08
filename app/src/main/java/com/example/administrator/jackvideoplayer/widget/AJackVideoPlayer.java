package com.example.administrator.jackvideoplayer.widget;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/11/7.
 */

public abstract class AJackVideoPlayer extends FrameLayout implements View.OnTouchListener {
    public AudioManager mAudioManager;
    public Timer updateSeekbatTimer;
    public TimerTask updateSeekbatTimerTask;

    private static final int THERSHOLD=80;
    private boolean mNeedChangePosition;
    private boolean mNeedChangeVolume;
    private boolean mNeedChangeBrightness;

    private float mDownX;
    private float mDownY;

    //计算播放进度条
    public abstract void updateTimeSeek();

    public AJackVideoPlayer(@NonNull Context context) {
        super(context);
        mAudioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        this.setOnTouchListener(this);
    }

    public void startUpdateTimeSeekBar(){
        cancelUpdateTimeSeekBar();
        if(updateSeekbatTimer==null){
            updateSeekbatTimer=new Timer();
        }
        if(updateSeekbatTimerTask==null){
            updateSeekbatTimerTask=new TimerTask() {
                @Override
                public void run() {
                    AJackVideoPlayer.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateTimeSeek();
                        }
                    });
                }
            };
        }
        updateSeekbatTimer.schedule(updateSeekbatTimerTask,0,1000);
    }

    public void cancelUpdateTimeSeekBar(){
        if(updateSeekbatTimer!=null){
            updateSeekbatTimer.cancel();
            updateSeekbatTimer=null;
        }
        if(updateSeekbatTimerTask!=null){
            updateSeekbatTimerTask.cancel();
            updateSeekbatTimerTask=null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e("onTouch",event.getX()+"");
        float x=event.getX();
        float y=event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX=x;
                mDownY=y;
                mNeedChangePosition=false;
                mNeedChangeVolume=false;
                mNeedChangeBrightness=false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX=x-mDownX;
                float deltaY=y-mDownY;
                float absDeltaX=Math.abs(deltaX);
                float absDeltaY=Math.abs(deltaY);

                if(!mNeedChangePosition&&!mNeedChangeVolume&&!mNeedChangeBrightness){
                    if(absDeltaX>=THERSHOLD){
                        mNeedChangePosition=true;
                    }else if(absDeltaY>=THERSHOLD){
                        if(mDownX<getWidth()*0.5f){
                            mNeedChangeBrightness=true;
                        }else{
                            mNeedChangeVolume=true;
                        }
                    }
                }
                if(mNeedChangePosition){
                    Log.e("ACTION_MOVE","THERSHOLD");
                }
                if(mNeedChangeBrightness){
                    Log.e("ACTION_MOVE","THERSHOLD2");
                }
                if(mNeedChangeVolume){
                    Log.e("ACTION_MOVE","THERSHOLD3");
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(mNeedChangePosition){

                    return true;
                }
                if(mNeedChangeBrightness){

                    return true;
                }
                if(mNeedChangeVolume){

                    return true;
                }
                break;
        }
        return false;
    }


}
