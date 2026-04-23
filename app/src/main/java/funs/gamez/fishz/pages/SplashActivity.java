package funs.gamez.fishz.pages;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import funs.gamez.fishz.GameApp;
import funs.gamez.fishz.R;
import funs.gamez.fishz.helps.Consts;
import funs.gamez.fishz.helps.SoundManage;
import funs.gamez.fishz.helps.ThreadPool;

public class SplashActivity extends Activity {

    private boolean horz = false;

    SoundManage s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViewById(R.id.start_arcade_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent game = new Intent(SplashActivity.this, PlayGameActivity.class);
                game.putExtra(PlayGameActivity.GAME_TYPE, PlayGameActivity.GAME_TYPE_ARCADE);
                game.putExtra(PlayGameActivity.GAME_HORZ, horz);
                startActivity(game);
            }
        });
        s = GameApp.getInstance(this).getSoundEffects();


        findViewById(R.id.open_prefs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p = new Intent(SplashActivity.this, SettingsActivity.class);
                startActivity(p);
            }
        });


    }

    @Override
    protected void onPause() {
        s.pauseBGMusic();
        //s.releaseBGM();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        s.playBGMusic(0);

        // 读取得分
        TextView scorelabel = findViewById(R.id.high_score);
        SharedPreferences prefs = getSharedPreferences(Consts.spName, MODE_PRIVATE);
        int scoreLast = prefs.getInt(Consts.spKeyLastScore,0);
        int scoreMax = prefs.getInt(Consts.spKeyHighestScore,0);
        scorelabel.setText("");
        if (scoreLast>0) {
            scorelabel.append(getString(R.string.last_score_lab, scoreLast));
            if (scoreMax>0) {
                scorelabel.append("\n");
                scorelabel.append(getString(R.string.high_score_lab2, scoreMax));
            }
        }
    }

    @Override
    protected void onDestroy() {
        ThreadPool.unInit();
        super.onDestroy();
    }
}
