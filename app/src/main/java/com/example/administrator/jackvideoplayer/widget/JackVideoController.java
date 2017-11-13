package com.example.administrator.jackvideoplayer.widget;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.jackvideoplayer.R;

/**
 * Created by Administrator on 2017/11/7.
 */
public class JackVideoController extends AJackVideoPlayer implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private TextView title;
    private SeekBar seekBar;
    private ImageView open;
    private ImageView screen;
    private TextView mTimeAll;
    private TextView mTimeImg;
    private TextView scrollData;
    private ImageView tipImage;
    private TextView progressbarTime;
    private RelativeLayout timeProgress;
    private RelativeLayout controllerView;
    private RelativeLayout controllerBottomView;
    private RelativeLayout controllerTopView;
    private boolean playStatus=true;
    private boolean screenStatus=true;

    private long duration;

    public JackVideoController(@NonNull Context context) {
        super(context);
        initController(context);
    }

    public void initController(Context context){
        this.context=context;
        mRoot= LayoutInflater.from(context).inflate(R.layout.layout_controller,this,false);
        addView(mRoot);
        scrollData=(TextView)findViewById(R.id.sound_data);
        tipImage=(ImageView)findViewById(R.id.ic_tip_img);
        title=(TextView)findViewById(R.id.av_title);
        open=(ImageView)findViewById(R.id.play_btn);
        seekBar=(SeekBar)findViewById(R.id.av_seek_bar);
        screen=(ImageView)findViewById(R.id.screen_btn);
        mTimeAll=(TextView)findViewById(R.id.time_all);
        mTimeImg=(TextView)findViewById(R.id.time_img);
        timeProgress=(RelativeLayout)findViewById(R.id.time_progress);
        controllerView=(RelativeLayout)findViewById(R.id.controller_view);
        controllerBottomView=(RelativeLayout)findViewById(R.id.controller_bottom_view);
        controllerTopView=(RelativeLayout)findViewById(R.id.controller_top_view);
        progressbarTime=(TextView)findViewById(R.id.progressbar_time);
        open.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        screen.setOnClickListener(this);
//        mRoot.setOnClickListener(this);
    }

    public void setVideoView(JackVideoPlayer iMediaPlayer){
        this.iMediaPlayer=iMediaPlayer;
    }

    @Override
    public void updateTimeSeek() {
        long position=iMediaPlayer.getCurrentPosition();
        duration=iMediaPlayer.getDuration();
        long bufferPercentage=iMediaPlayer.getCurrentPosition();
        seekBar.setSecondaryProgress((int)bufferPercentage);
        int progress=(int) (100f*position/duration);
        seekBar.setProgress(progress);

        mTimeImg.setText(VideoViewUtil.formatTime(position));
        mTimeAll.setText(VideoViewUtil.formatTime(duration));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.play_btn:
                playStatus=!playStatus;
                updatePlayBtn();
                if(playStatus){
                    iMediaPlayer.restart();
                }else{
                    iMediaPlayer.pause();
                }
                break;
            case R.id.screen_btn:
                if(screenStatus){
                    iMediaPlayer.enterFullScreen();
                }else{
                    iMediaPlayer.exitFullScreen();
                }
                screenStatus=!screenStatus;
                break;
            default:
//              controllerStatus=!controllerStatus;
//              onTouchUpdate();
              break;
        }
    }

    public void udpateControllState(int state){
        switch(state){
            //播放未开始
            case IJackVideoPalyer.STATE_IDLE:

                break;
            // 播放准备中
            case IJackVideoPalyer.STATE_PREPARING:

                break;
            // STATE_PREPARED
            case IJackVideoPalyer.STATE_PREPARED:

                break;
            // 正在播放
            case IJackVideoPalyer.STATE_PLAYING:
                playStatus=true;
                updatePlayBtn();
                startUpdateTimeSeekBar();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        controllerBottomView.setVisibility(View.GONE);
//                        controllerTopView.setVisibility(View.GONE);
//                        controllerStatus=false;
//                    }
//                },2000);
                break;
            // 暂停播放
            case IJackVideoPalyer.STATE_PAUSED:
                open.setImageResource(R.mipmap.play_stop);
                break;
            // 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
            case IJackVideoPalyer.STATE_BUFFERING_PLAYING:

                break;
            //正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
            case IJackVideoPalyer.STATE_BUFFERING_PAUSED:

                break;
            //播放错误
            case IJackVideoPalyer.STATE_ERROR:

                break;
            case IJackVideoPalyer.STATE_COMPLETED:
            // 播放完成

                break;
        }
    }

    @Override
    public void onTouchUpdate() {
        if(controllerStatus==true){
            controllerBottomView.setVisibility(View.VISIBLE);
            controllerTopView.setVisibility(View.VISIBLE);
        }else{
            controllerBottomView.setVisibility(View.GONE);
            controllerTopView.setVisibility(View.GONE);
        }
    }

    public void updateControllerModel(int model){
        switch(model){
            case IJackVideoPalyer.MODE_NORMAL:

                break;
            case IJackVideoPalyer.MODE_FULL_SCREEN:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controllerBottomView.setVisibility(View.GONE);
                        controllerTopView.setVisibility(View.GONE);
                    }
                },2000);
                break;
        }
    }

    public void updatePlayBtn(){
        if(playStatus){
            open.setImageResource(R.mipmap.play_ing);
        }else{
            open.setImageResource(R.mipmap.play_stop);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long position = (long)(iMediaPlayer.getDuration() * seekBar.getProgress() / 100f);
        iMediaPlayer.seekTo(position);
        startUpdateTimeSeekBar();
    }

    @Override
    protected void showChangePosition(long duration, int newPositionProgress) {
        if(timeProgress.getVisibility()==View.GONE){
            timeProgress.setVisibility(View.VISIBLE);
        }
        Log.e("duration1",duration+"");
        Log.e("duration2",Double.valueOf(newPositionProgress).doubleValue()/100+"");

        double data=duration*(Double.valueOf(newPositionProgress).doubleValue()/100);
        progressbarTime.setText(VideoViewUtil.formatTime((long)data));
    }

    @Override
    protected void hideChangePosition() {
        if(timeProgress.getVisibility()==View.VISIBLE){
            timeProgress.setVisibility(View.GONE);
        }
    }

    @Override
    protected void showChangeVolume(int newVolumeProgress) {
        showControllView(R.mipmap.ic_sound,newVolumeProgress+"");
    }

    @Override
    protected void hideChangeVolume() {
        hideControllView();
    }

    @Override
    protected void showChangeBrightness(int newBrightnessProgress) {
        showControllView(R.mipmap.ic_brightness,newBrightnessProgress+"");
    }

    @Override
    protected void hideChangeBrightness() {
        hideControllView();
    }

    public void showControllView(int drawable,String data){
        Log.e("data",data+"");
        if(controllerView.getVisibility()==View.GONE){
            controllerView.setVisibility(View.VISIBLE);
            tipImage.setImageResource(drawable);
        }
        scrollData.setText(data);
    }

    public void hideControllView(){
        if(controllerView.getVisibility()==View.VISIBLE){
            controllerView.setVisibility(View.GONE);
        }
    }

    public void reset(){
        playStatus=true;
        screenStatus=true;
        open.setImageResource(R.mipmap.play_stop);
        seekBar.setProgress(0);
    }

}
