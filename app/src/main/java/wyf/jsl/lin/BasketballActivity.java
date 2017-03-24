package wyf.jsl.lin;

import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import static wyf.jsl.lin.Constant.*;

public class BasketballActivity extends Activity {
    private GLGameView gameplay;
    public MenuView gamemenu;
    private AboutView gameabout;
    private HelpView gamehelp;
    private OverView gameover;
    private SoundView gamesound;

    Handler hd;

    MediaPlayer mpBack;
    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);
        new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hd.sendEmptyMessage(GAME_SOUND);
            }
        }.start();

        initSounds();

        hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case GAME_SOUND:
                        gamesound = new SoundView(BasketballActivity.this);
                        setContentView(gamesound);
                        break;
                    case GAME_MENU:
                        MENU_FLAG = true;
                        gamemenu = new MenuView(BasketballActivity.this);
                        setContentView(gamemenu);
                        break;
                    case GAME_LOAD:
                        MENU_FLAG = false;
                        setContentView(R.layout.loading);
                        new Thread() {
                            public void run() {
                                try {
                                    sleep(2000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                hd.sendEmptyMessage(GAME_PLAY);
                            }
                        }.start();
                        break;
                    case GAME_PLAY:
                        score = 0;
                        deadtimes = 60;
                        SOUND_FLAG = SOUND_MEMORY;
                        DEADTIME_FLAG = true;
                        new DeadtimeThread(BasketballActivity.this).start();

                        gameplay = new GLGameView(BasketballActivity.this, gamemenu);
                        gameplay.requestFocus();
                        gameplay.setFocusableInTouchMode(true);
                        if (SOUND_FLAG) {
                            mpBack.setLooping(true);
                            mpBack.setVolume(0.2f, 0.2f);
                            mpBack.start();
                        }
                        setContentView(gameplay);
                        break;
                    case GAME_ABOUT:
                        MENU_FLAG = false;
                        gameabout = new AboutView(BasketballActivity.this);
                        setContentView(gameabout);
                        break;
                    case GAME_HELP:
                        MENU_FLAG = false;
                        gamehelp = new HelpView(BasketballActivity.this);
                        setContentView(gamehelp);
                        break;
                    case GAME_OVER:
                        gameover = new OverView(BasketballActivity.this, gamemenu);
                        setContentView(gameover);
                        break;
                    case RETRY:
                        gamemenu = new MenuView(BasketballActivity.this);
                        setContentView(gamemenu);
                        break;
                }
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onResume() {
        super.onResume();
        if (gameplay != null) {
            gameplay.onResume();
            mpBack.start();
        }

    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onPause() {
        super.onPause();
        gameplay.onPause();
        mpBack.pause();
    }

    public void initSounds() {
        mpBack = MediaPlayer.create(this, R.raw.gameback);

        soundPool = new SoundPool
                (
                        4,
                        AudioManager.STREAM_MUSIC,
                        100
                );
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(1, soundPool.load(this, R.raw.collision, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.over, 1));

    }


    public void playSound(int sound, int loop) {

        AudioManager mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        float volume = streamVolumeCurrent / streamVolumeMax;

        soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 0.5f);
    }

    public void waitTwoSeconds() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}



