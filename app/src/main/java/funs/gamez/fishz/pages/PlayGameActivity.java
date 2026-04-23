package funs.gamez.fishz.pages;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import funs.gamez.fishz.GameApp;
import funs.gamez.fishz.R;
import funs.gamez.fishz.helps.Consts;
import funs.gamez.fishz.helps.SoundManage;
import funs.gamez.fishz.game.GameView;

import java.util.Date;

/*
    游戏页面
* */
public class PlayGameActivity extends Activity {

    public static final String GAME_TYPE = "GAMETYPE";
    public static final int GAME_TYPE_CLASSIC = 0;
    public static final int GAME_TYPE_ARCADE = 1;

    public static final String GAME_HORZ = "GAMEHORZ";


    final Handler handler = new Handler();
    private final int NEW_LIFE_EVERY = 5000;
    private final int MAX_LIVES = 10;


    private GameView mFishView;
    private int mPoints;
    private int mLives;


    private boolean mPaused;

    private SoundManage mSounds;

    private int gameType = GAME_TYPE_CLASSIC;
    private boolean gameHorz = false;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_main);

        mPrefs = getSharedPreferences(Consts.spName, MODE_PRIVATE);

        ActionBar b = getActionBar();
        if (b != null) b.hide();

        mFishView = (GameView) findViewById(R.id.fishscreen);

        mLives = 5;

        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPaused) {
                    unpause();
                } else {
                    pause();
                }
            }
        });

        gameType = getIntent().getIntExtra(GAME_TYPE, GAME_TYPE_CLASSIC);
        gameHorz = getIntent().getBooleanExtra(GAME_HORZ, false);
        // setOrientation();

        mFishView.setOnGameListener(new GameView.OnGameListener() {
            @Override
            public void onWaveStart(int wavenum) {
                mSounds.playBGMusic();
            }

            @Override
            public void onWaveDone(int wavenum) {
                mSounds.fadeBGMusic();
            }

            @Override
            public void onIntervalStart(int intervalnum) {

            }

            @Override
            public void onIntervalDone(int intervalnum) {

            }

            @Override
            public void onItemLaunch() {
                mSounds.playPuh();
            }

            @Override
            public void onItemHit(int points) {
                mSounds.playChop();
                if (mPoints % NEW_LIFE_EVERY > (mPoints + points) % NEW_LIFE_EVERY) {
                    if (mLives < MAX_LIVES) {
                        mLives++;
                        mSounds.playBest();
                    }
                }

                mPoints += points;
                updateScores();

            }

            @Override
            public void onCombo(int hits) {
                mSounds.playGood();
                if (hits > 2) {
                    mPoints += hits * 10;
                    updateScores();
                    mFishView.setText(getString(R.string.bonus));

                }
            }

            @Override
            public void onMiss(int points) {
                if (gameType == GAME_TYPE_CLASSIC) {
                    mSounds.playBad();
                    loseLife();
                }

                updateScores();
            }

            @Override
            public void onBoom() {
                mSounds.playBad();
                mFishView.setText(getString(R.string.boom));
                loseLife();
                updateScores();
            }
        });

        if (savedInstanceState != null) {
            unfreeze(savedInstanceState);
            unpause();
        }

    }


    private void loseLife() {
        mLives--;
        if (mLives <= 0) {
            mFishView.setText(getString(R.string.gameover));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFishView.setText(getString(R.string.gameover));
                    mFishView.endGame();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent entry = new Intent(PlayGameActivity.this, SplashActivity.class);
                            PlayGameActivity.this.finish();
                            startActivity(entry);
                        }
                    }, 2500);
                }
            }, 500);
            mLives = 0;
        }
    }

    // 暂停
    private void pause() {
        mPaused = true;
        mFishView.pauseGame();
    }

    // 恢复
    private void unpause() {
        mFishView.unpauseGame();
        mPaused = false;
    }

    // 更新得分
    private void updateScores() {
        mFishView.setTopStatus(String.valueOf(mPoints), mLives);
        if (mPoints > 0 && mPoints > mPrefs.getInt(Consts.spKeyHighestScore, 0)) {
            mPrefs.edit().putInt(Consts.spKeyHighestScore, mPoints).putLong(Consts.spKeyHighestScoreDate, new Date().getTime()).apply();
        }

        if (mPoints > 0) {
            mPrefs.edit().putInt(Consts.spKeyLastScore, mPoints).putLong(Consts.spKeyLastScoreDate, new Date().getTime()).apply();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        freeze(outState);
    }

    // 保存数据
    private void freeze(Bundle bundle) {
        bundle.putLong("freeztime", System.currentTimeMillis());

        bundle.putInt("mPoints", mPoints);
        bundle.putInt("mLives", mLives);

        Bundle fishview = new Bundle();
        mFishView.freeze(fishview);
        bundle.putBundle("fishview", fishview);
    }

    // 恢复数据
    private void unfreeze(Bundle bundle) {
        mPoints = bundle.getInt("mPoints");
        mLives = bundle.getInt("mLives");

        Bundle fishview = bundle.getBundle("fishview");

        mFishView.unfreeze(fishview);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pause();
                unpause();
            }
        }, 3000);

    }

    @Override
    protected void onPause() {

        mSounds.releaseBGM();
        pause();

        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        mSounds.releaseBGM();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        mSounds = GameApp.getInstance(this).getSoundEffects();


        unpause();

        updateScores();

        mSounds.playBGMusic();
    }

    @Override
    protected void onDestroy() {
        mSounds.releaseBGM();
        super.onDestroy();
    }

}
