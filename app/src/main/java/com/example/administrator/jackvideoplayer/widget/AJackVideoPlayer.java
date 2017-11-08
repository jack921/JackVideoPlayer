package com.example.administrator.jackvideoplayer.widget;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
    public JackVideoPlayer iMediaPlayer;

    public Context context;
    public View mRoot;

    private static final int THERSHOLD=80;
    private boolean mNeedChangePosition;
    private boolean mNeedChangeVolume;
    private boolean mNeedChangeBrightness;
    private float mDownX;
    private float mDownY;

    private long mGestureDownPosition;
    private float mGestureDownBrightness;
    private int mGestureDownVolume;
    private long mNewPosition;

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
                        cancelUpdateTimeSeekBar();
                        mNeedChangePosition=true;
                        mGestureDownPosition=iMediaPlayer.getCurrentPosition();
                    }else if(absDeltaY>=THERSHOLD){
                        if(mDownX<getWidth()*0.5f){
                            mNeedChangeBrightness=true;
                            mGestureDownBrightness=VideoViewUtil.scanForActivity(context).getWindow().getAttributes().screenBrightness;
                        }else{
                            mNeedChangeVolume=true;
                            mGestureDownVolume=iMediaPlayer.getVolume();
                        }
                    }
                }
                if(mNeedChangePosition){
                    long duration=iMediaPlayer.getDuration();
                    long toPosition=(long)(mGestureDownPosition+duration*deltaX/getWidth());
                    mNewPosition=Math.max(0,Math.min(duration,toPosition));
                    int newPositionProgress=(int) (100f*mNewPosition/duration);
                    showChangePosition(duration,newPositionProgress);
                }
                if(mNeedChangeBrightness){
                    Log.e("ACTION_MOVE","THERSHOLD2");
                    deltaY=-deltaY;
                    float detlaBrightness=deltaY*3/getHeight();
                    float newBrightness=mGestureDownBrightness+detlaBrightness;
                    newBrightness=Math.max(0,Math.min(newBrightness,1));
                    float newBrightnessPercentage=newBrightness;
                    WindowManager.LayoutParams params=VideoViewUtil.scanForActivity(context).getWindow().getAttributes();
                    params.screenBrightness=newBrightnessPercentage;
                    VideoViewUtil.scanForActivity(context).getWindow().setAttributes(params);
                    int newBrightnessProgress=(int)(100f*newBrightnessPercentage);
                    showChangeBrightness(newBrightnessProgress);
                }
                if(mNeedChangeVolume){
                    Log.e("ACTION_MOVE","THERSHOLD3");
                    deltaY=-deltaY;
                    int maxVolume=iMediaPlayer.getMaxVolume();
                    int deltaVolume=(int)(maxVolume*deltaY*3/getHeight());
                    int newVolume=mGestureDownVolume+deltaVolume;
                    newVolume=Math.max(0,Math.min(maxVolume,newVolume));
                    iMediaPlayer.setVolume(newVolume);
                    int newVolumeProgress=(int)(100f*newVolume/maxVolume);
                    showChangeVolume(newVolumeProgress);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(mNeedChangePosition){
                    iMediaPlayer.seekTo(mNewPosition);
                    hideChangePosition();
                    startUpdateTimeSeekBar();
                    return true;
                }
                if(mNeedChangeBrightness){
                    hideChangeBrightness();
                    return true;
                }
                if(mNeedChangeVolume){
                    hideChangeVolume();
                    return true;
                }
                break;
        }
        return false;
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

    /**
     * 手势左右滑动改变播放位置时，显示控制器中间的播放位置变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param duration            视频总时长ms
     * @param newPositionProgress 新的位置进度，取值0到100。
     */
    protected abstract void showChangePosition(long duration, int newPositionProgress);

    /**
     * 手势左右滑动改变播放位置后，手势up或者cancel时，隐藏控制器中间的播放位置变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangePosition();

    /**
     * 手势在右侧上下滑动改变音量时，显示控制器中间的音量变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newVolumeProgress 新的音量进度，取值1到100。
     */
    protected abstract void showChangeVolume(int newVolumeProgress);

    /**
     * 手势在左侧上下滑动改变音量后，手势up或者cancel时，隐藏控制器中间的音量变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeVolume();

    /**
     * 手势在左侧上下滑动改变亮度时，显示控制器中间的亮度变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newBrightnessProgress 新的亮度进度，取值1到100。
     */
    protected abstract void showChangeBrightness(int newBrightnessProgress);

    /**
     * 手势在左侧上下滑动改变亮度后，手势up或者cancel时，隐藏控制器中间的亮度变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeBrightness();


}
