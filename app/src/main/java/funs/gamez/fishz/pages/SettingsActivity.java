package funs.gamez.fishz.pages;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import funs.gamez.fishz.GameApp;
import funs.gamez.fishz.R;

/**
 * 游戏设置
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getListView().setBackgroundColor(Color.WHITE);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

//        final String clzName = getClass().getName();
//        Log.d("SettingsActivity", "clzName: " +clzName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GameApp.getInstance(this).getSoundEffects().playBGMusic(0);
    }

    @Override
    protected void onPause() {
        GameApp.getInstance(this).getSoundEffects().pauseBGMusic();
        super.onPause();
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        final String clzName = getClass().getName();
        Log.d("SettingsActivity", "clzName: " +clzName+" ; isValidFragment:" + fragmentName);
        if (null!=fragmentName && fragmentName.startsWith(clzName)) {
            return true;
        }
        return super.isValidFragment(fragmentName);
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }

    }


}
