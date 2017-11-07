package com.example.administrator.jackvideoplayer.widget;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/11/7.
 */

public abstract class AJackVideoPlayer extends FrameLayout{
    public AudioManager mAudioManager;
    public Timer updateSeekbatTimer;
    public TimerTask updateSeekbatTimerTask;

    //计算播放进度条
    public abstract void updateTimeSeek();

    public AJackVideoPlayer(@NonNull Context context) {
        super(context);
        mAudioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
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


}
