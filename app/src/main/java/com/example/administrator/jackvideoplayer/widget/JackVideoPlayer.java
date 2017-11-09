package com.example.administrator.jackvideoplayer.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.io.IOException;
import java.util.Map;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Administrator on 2017/11/7.
 */
public class JackVideoPlayer extends FrameLayout implements IJackVideoPalyer,
        IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener, TextureView.SurfaceTextureListener {
    public final String TAG="jack";

    private FrameLayout mContainer;
    private IMediaPlayer mMediaPlayer;
    private int mCurrentState = STATE_IDLE;
    private int mCurrentMode=MODE_NORMAL;
    private JackVideoController jackVideoController;
    private SurfaceTexture mSurfaceTexture;
    private AudioManager mAudioManager;
    private TextureView textureView;
    private int mBufferPercentage;
    private Surface surface;
    private Context context;
    private String uri;

    public JackVideoPlayer(Context context) {
        super(context);
        initVideoPlayer(context);
    }

    public JackVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoPlayer(context);
    }

    public JackVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoPlayer(context);
    }

    public void initVideoPlayer(Context context){
        this.context=context;
        initView();
        initAudioManager();
        initMediaPlayer();
    }

    public void initView(){
        mContainer=new FrameLayout(context);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mContainer,params);
    }

    public void initAudioManager(){
        if (context instanceof Activity) {
            ((Activity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    public void initMediaPlayer(){
        try{
            mMediaPlayer=new IjkMediaPlayer();
            mMediaPlayer.setLogEnabled(false);
            ((IjkMediaPlayer)mMediaPlayer).setOption(1, "analyzemaxduration", 100L);
            ((IjkMediaPlayer)mMediaPlayer).setOption(1, "probesize", 10240L);
            ((IjkMediaPlayer)mMediaPlayer).setOption(1, "flush_packets", 1L);
            ((IjkMediaPlayer)mMediaPlayer).setOption(4, "packet-buffering", 0L);
            ((IjkMediaPlayer)mMediaPlayer).setOption(4, "framedrop", 1L);

            //设置监听
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
        }catch(Exception e){
        }
    }

    public void openUri(String uri){
        if(uri==null){
            Toast.makeText(context,"uri不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        this.uri=uri;
        initTexttureView();
        addTextureView();
    }

    public void initTexttureView(){
        if(textureView==null){
            textureView=new TextureView(context);
            textureView.setSurfaceTextureListener(this);
        }
    }

    public void addTextureView(){
        mContainer.removeView(textureView);
        LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mContainer.addView(textureView,0,params);
    }

    public void setVideoController(JackVideoController jackVideoController){
        this.jackVideoController=jackVideoController;
        jackVideoController.setVideoView(this);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(jackVideoController,params);
    }

    public void openMediaPlayer(){
        try {
            if(surface==null){
                surface=new Surface(mSurfaceTexture);
            }
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setDataSource(context, Uri.parse(uri));
            mMediaPlayer.prepareAsync();
            mMediaPlayer.start();
        } catch (IOException e) {
            Log.e("openMediaPlayer",e.getMessage());
        }
    }

    @Override
    public void setUp(String url, Map<String, String> headers) {

    }

    @Override
    public void start() {

    }

    @Override
    public void start(long position) {

    }

    @Override
    public void restart() {
        if(mCurrentState==STATE_PAUSED){
            mMediaPlayer.start();
            mCurrentState=STATE_PLAYING;
            jackVideoController.udpateControllState(mCurrentState);
        }else if(mCurrentState==STATE_BUFFERING_PAUSED){
            mMediaPlayer.start();
            mCurrentState=STATE_BUFFERING_PLAYING;
            jackVideoController.udpateControllState(mCurrentState);
        }else if(mCurrentState==STATE_COMPLETED||mCurrentState==STATE_ERROR){
            mMediaPlayer.reset();
            initTexttureView();
            addTextureView();
        }else{
            Log.e(TAG,"retart error");
        }
    }

    @Override
    public void pause() {
        if(mCurrentState==STATE_PLAYING){
            mMediaPlayer.pause();
            mCurrentState=STATE_PAUSED;
            jackVideoController.udpateControllState(mCurrentState);
        }
        if(mCurrentState==STATE_BUFFERING_PLAYING){
            mMediaPlayer.pause();
            mCurrentState=STATE_BUFFERING_PAUSED;
            jackVideoController.udpateControllState(mCurrentState);
        }
    }

    @Override
    public void seekTo(long pos) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(pos);
        }
    }

    @Override
    public void setVolume(int volume) {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            ((IjkMediaPlayer) mMediaPlayer).setSpeed(speed);
        } else {
            Toast.makeText(context,"只有IjkPlayer才能设置播放速度",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void continueFromLastPosition(boolean continueFromLastPosition) {

    }

    @Override
    public boolean isIdle() {
        return mCurrentState==STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState==STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState==STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState==STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState==STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState==STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState==STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState==STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState==STATE_COMPLETED;
    }

    @Override
    public boolean isFullScreen() {
        return mCurrentState==MODE_FULL_SCREEN;
    }

    @Override
    public boolean isNormal() {
        return mCurrentState==MODE_NORMAL;
    }

    @Override
    public int getMaxVolume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public int getVolume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public long getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    @Override
    public long getCurrentPosition() {
        return  mMediaPlayer!= null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public float getSpeed(float speed) {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mMediaPlayer).getSpeed(speed);
        }
        return 0;
    }

    @Override
    public long getTcpSpeed() {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mMediaPlayer).getTcpSpeed();
        }
        return 0;
    }

    @Override
    public void enterFullScreen() {
        if(mCurrentMode==MODE_FULL_SCREEN){
            return;
        }
        VideoViewUtil.hideActionBar(context);
        VideoViewUtil.scanForActivity(context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup contentView=VideoViewUtil.scanForActivity(context).findViewById(android.R.id.content);
        this.removeView(mContainer);
        LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer,params);
        mCurrentMode=MODE_FULL_SCREEN;
        jackVideoController.updateControllerModel(mCurrentMode);
    }

    @Override
    public boolean exitFullScreen() {
        if(mCurrentMode==MODE_FULL_SCREEN){
            VideoViewUtil.showActionBar(context);
            VideoViewUtil.scanForActivity(context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ViewGroup contentView=VideoViewUtil.scanForActivity(context).findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            ViewGroup.LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer,params);
            mCurrentMode=MODE_NORMAL;
            jackVideoController.updateControllerModel(mCurrentState);
        }
        return false;
    }

    @Override
    public void releasePlayer() {
        if(mAudioManager!=null){
            mAudioManager.abandonAudioFocus(null);
            mAudioManager=null;
        }
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
        mContainer.removeView(textureView);
        if(mSurfaceTexture!=null){
            mSurfaceTexture.release();
            mSurfaceTexture=null;
        }
        mCurrentState=STATE_IDLE;
    }

    @Override
    public void release() {
        if(isPlaying()||isBufferingPlaying()||isBufferingPaused()||isPaused()){
            VideoViewUtil.savePlayPosition(context,uri,getCurrentPosition());
        }else if(isCompleted()){
            VideoViewUtil.savePlayPosition(context,uri,0);
        }
        releasePlayer();
        if(jackVideoController!=null){
            jackVideoController.reset();
        }
        Runtime.getRuntime().gc();
    }

    //准备回调
    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {

    }

    //大小改变回调
    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

    }

    //完成回调
    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mCurrentState=IJackVideoPalyer.STATE_COMPLETED;
        jackVideoController.udpateControllState(mCurrentState);
    }

    //报错回调
    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        Toast.makeText(context,"播放错误！",Toast.LENGTH_SHORT).show();
        return false;
    }

    //播放信息回调
    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        switch(what){
            case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                // 播放器开始渲染
                mCurrentState=STATE_PLAYING;
                jackVideoController.udpateControllState(mCurrentState);
                break;
            case IjkMediaPlayer.MEDIA_INFO_BUFFERING_START:
                // MediaPlayer暂时不播放，以缓冲更多的数据
                if(mCurrentState==STATE_PAUSED||mCurrentState==STATE_BUFFERING_PAUSED){
                    mCurrentState=STATE_BUFFERING_PAUSED;
                }else{
                    mCurrentState=STATE_BUFFERING_PLAYING;
                }
                jackVideoController.udpateControllState(mCurrentState);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if(mCurrentState==STATE_BUFFERING_PLAYING){
                    mCurrentState=STATE_PLAYING;
                    jackVideoController.udpateControllState(mCurrentState);
                }
                if(mCurrentState==STATE_BUFFERING_PAUSED){
                    mCurrentState=STATE_PAUSED;
                    jackVideoController.udpateControllState(mCurrentState);
                }
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                // 视频旋转了extra度，需要恢复
                if(textureView!=null){
                    textureView.setRotation(extra);
                }
                break;
            case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                //视频不能seekTo，为直播视频
                Toast.makeText(context,"视频不能seekTo,为直播视频",Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    //缓存对调
    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        mBufferPercentage = percent;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            openMediaPlayer();
        } else {
            textureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture==null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


}
