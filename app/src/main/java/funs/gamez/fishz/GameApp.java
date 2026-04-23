package funs.gamez.fishz;

import android.app.Application;
import android.content.Context;

import funs.gamez.fishz.helps.SoundManage;

/**
 * Application
 */
public class GameApp extends Application {

    public static GameApp getInstance(Context context) {
        return (GameApp) context.getApplicationContext();
    }

    private SoundManage mSoundEffects;


    public SoundManage getSoundEffects() {
        return mSoundEffects;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSoundEffects = new SoundManage(this);
    }

    @Override
    public void onTerminate() {
        mSoundEffects.release();
        super.onTerminate();
    }

    public boolean check(Context context) {
        return context.getApplicationContext() == this.getApplicationContext();
    }
}
