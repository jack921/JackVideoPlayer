package com.example.administrator.jackvideoplayer.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.administrator.jackvideoplayer.R;

/**
 * Created by Administrator on 2017/11/7.
 */

public class JackVideoController extends AJackVideoPlayer implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Context context;
    private View mRoot;

    private TextView title;
    private SeekBar seekBar;
    private ImageView open;
    private ImageView screen;
    private TextView mTimeAll;
    private TextView mTimeImg;
    private JackVideoPlayer iMediaPlayer;
    private boolean playStatus=true;
    private boolean screenStatus=true;

    public JackVideoController(@NonNull Context context) {
        super(context);
        initController(context);
    }

    public void initController(Context context){
        this.context=context;
        mRoot= LayoutInflater.from(context).inflate(R.layout.layout_controller,this,false);
        addView(mRoot);

        title=(TextView)findViewById(R.id.av_title);
        open=(ImageView)findViewById(R.id.play_btn);
        seekBar=(SeekBar)findViewById(R.id.av_seek_bar);
        screen=(ImageView)findViewById(R.id.screen_btn);
        mTimeAll=(TextView)findViewById(R.id.time_all);
        mTimeImg=(TextView)findViewById(R.id.time_img);

        open.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        screen.setOnClickListener(this);

    }

    public void setVideoView(JackVideoPlayer iMediaPlayer){
        this.iMediaPlayer=iMediaPlayer;
    }

    @Override
    public void updateTimeSeek() {
        long position=iMediaPlayer.getCurrentPosition();
        long duration=iMediaPlayer.getDuration();
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
                break;
            // 暂停播放
            case IJackVideoPalyer.STATE_PAUSED:

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
        Log.e("psoition", position+"");
        iMediaPlayer.seekTo(position);
        startUpdateTimeSeekBar();
    }


}
