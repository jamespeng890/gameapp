package com.gamedev.match3.effect;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 * 音效管理器 - 使用SoundPool实现低延迟音效播放
 */
public class SoundManager {
    private SoundPool soundPool;
    private int matchSoundId = -1;
    private int clearSoundId = -1;
    private int levelUpSoundId = -1;
    private AudioManager audioManager;

    private boolean soundsLoaded = false;
    private float volumeLevel = 0.5f;

    public SoundManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initSoundPool();
    }

    /**
     * 初始化SoundPool
     */
    private void initSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        // 设置加载监听器
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    soundsLoaded = true;
                }
            }
        });
    }

    /**
     * 加载音效（使用默认的简单提示音）
     */
    public void loadSounds(Context context) {
        // 注意：实际应用中应该使用真实的音频文件
        // 这里仅作为演示，省略具体的音频加载代码
        // 在实际项目中，需要在res/raw目录放置音频文件
        soundsLoaded = true;
    }

    /**
     * 播放匹配音效
     */
    public void playMatchSound() {
        if (!soundsLoaded) return;
        
        float volume = getSystemVolume();
        if (matchSoundId >= 0) {
            soundPool.play(matchSoundId, volume, volume, 1, 0, 1.0f);
        } else {
            // 使用系统默认声音
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
        }
    }

    /**
     * 播放消除音效
     */
    public void playClearSound() {
        if (!soundsLoaded) return;
        
        float volume = getSystemVolume();
        if (clearSoundId >= 0) {
            soundPool.play(clearSoundId, volume, volume, 1, 0, 1.2f);
        } else {
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
        }
    }

    /**
     * 播放关卡升级音效
     */
    public void playLevelUpSound() {
        if (!soundsLoaded) return;
        
        float volume = getSystemVolume();
        if (levelUpSoundId >= 0) {
            soundPool.play(levelUpSoundId, volume, volume, 1, 0, 1.0f);
        } else {
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
        }
    }

    /**
     * 获取系统音量
     */
    private float getSystemVolume() {
        float current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return (current / max) * volumeLevel;
    }

    /**
     * 设置音量
     */
    public void setVolume(float level) {
        this.volumeLevel = Math.max(0, Math.min(level, 1.0f));
    }

    /**
     * 释放资源
     */
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
