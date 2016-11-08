package kevinhan.captainhypothesis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.ImageView;

import android.media.SoundPool;
import android.media.AudioManager;

/**
 * Created by Kevin on 2016-11-02.
 */

public class PlayView extends ImageView {
    private BitmapDrawable playerDraw, enemyDraw, laserDraw, appleDraw, shieldDraw, laser2Draw, explosionDraw
            , bomb1Draw, bomb2Draw, boomDraw;
    private Context mContext;
    private boolean beginFight;
    private int playerDrawX, playerDrawY, enemyDrawX, enemyDrawY, shieldDrawX, shieldDrawY
            , bombDrawX, bombDrawY;
    private int enemyExpDrawX, enemyExpDrawY, playerExpDrawX, playerExpDrawY;
    private Handler h;
    private final int FRAME_RATE = 30;
    Paint paint;

    private int enemyHP, playerHP;
    private int laserDrawX, laserDrawY, appleDrawX, appleDrawY, laser2DrawX, laser2DrawY;
    private int laserSpeed, appleSpeed, laser2Speed, bombSpeed;
    private static boolean laserVisible, appleVisible, shieldVisible, laser2Visible, bombVisible;
    private boolean enemyExplode, playerExplode, enemyExplodeAnim, playerExplodeAnim
            , bomb1Anim, bomb2Anim, bombAnimStart, bombExplode, appleCooldown;

    private static CountDownTimer shieldTimer;
    Animation anim;
    private CountDownTimer enemyExplodeTimer, playerExplodeTimer, bombTimer, bombExplodeTimer, appleTimer;

    //sound stuff
    private SoundPool soundEffects;
    private AudioManager audioManager;
    private int explosionSfx, laserSfx, shieldSfx, appleSfx;
    private float curVolume, maxVolume, volume;
    private static boolean explosionPlay, laserPlay, shieldPlay;

    public PlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        h = new Handler();

        prepBattle();

    }

    public void prepBattle() {
        playerDraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.ic_launcher);
        enemyDraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.enemy);
        explosionDraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.explosion);
        beginFight = false;

        shieldDraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.shield_ability);
        laserDraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.laser);
        appleDraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.apple);
        bomb1Draw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.bomb1);
        bomb2Draw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.bomb2);
        boomDraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.boom);

        laserSpeed = 20;
        appleSpeed = 15;
        laser2Speed = 10;
        bombSpeed = 25;
        laserVisible = false;
        appleVisible = false;
        shieldVisible = false;
        laser2Visible = false;
        bombVisible = false;
        bomb1Anim = false;
        bomb2Anim = false;
        bombAnimStart = false;

        appleCooldown = false;

        enemyHP = 20;
        playerHP = 20;

        laser2Draw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.laser2);

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(5);

        soundEffects = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        AudioManager audioManager = (AudioManager)mContext.getSystemService(mContext.AUDIO_SERVICE);
        explosionSfx = soundEffects.load(mContext, R.raw.explosion, 1);
        laserSfx = soundEffects.load(mContext, R.raw.laser, 1);
        shieldSfx = soundEffects.load(mContext, R.raw.shield, 1);
        appleSfx = soundEffects.load(mContext, R.raw.apple, 1);
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = curVolume/maxVolume;
        explosionPlay = false;
        laserPlay = false;
        shieldPlay = false;
    }

    public static void shootLaser() {
        if(!laserVisible) {
            laserVisible = true;
            laserPlay = true;
        }
    }
    public void startApple() {
        appleTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                appleVisible = false;
                appleCooldown = false;
            }
        }.start();
    }

    public static void shootApple() {
        if(!appleVisible) {
            appleVisible = true;
        }
    }

    public static void activateShield() {
        if(!shieldVisible) {
            shieldVisible = true;
            shieldPlay = true;
            shieldTimer = new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    shieldVisible = false;

                }
            }.start();
        }
    }

    public static void throwBomb() {
        if(!bombVisible) {
            bombVisible = true;
        }
    }

    public void startBomb() {
        bombTimer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(bomb1Anim) {
                    bomb1Anim = false;
                    bomb2Anim = true;
                }else {
                    bomb1Anim = true;
                    bomb2Anim = false;
                }
            }

            @Override
            public void onFinish() {
                bomb1Anim = false;
                bomb2Anim = false;
                bombVisible = false;
                bombAnimStart = false;
                startBombExplode();
                enemyHP -= 10;
            }
        }.start();
    }
    public void startBombExplode() {
        bombExplode = true;
        playExplosion();
        bombExplodeTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                bombExplode = false;
                resetBomb();
            }
        }.start();
    }

    public void startEnemyExplode() {
        enemyExplodeAnim = true;
        enemyExplodeTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                enemyExplodeAnim = false;
            }
        }.start();
    }
    public void startPlayerExplode() {
        playerExplodeAnim = true;
        enemyExplodeTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                playerExplodeAnim = false;
            }
        }.start();
    }

    //sound methods
    public void playLaser() {
        soundEffects.play(laserSfx, volume, volume, 1, 0, 1f);
        laserPlay = false;
    }
    public void playExplosion() {
        soundEffects.play(explosionSfx, volume, volume, 1, 0, 1f);
        explosionPlay = false;
    }
    public void playShield() {
        soundEffects.play(shieldSfx, volume, volume, 1, 0, 1f);
        shieldPlay = false;
    }
    public void playApple() {
        soundEffects.play(appleSfx, volume, volume, 1, 0, 1f);
    }
    ////////////////

    public void resetLaser() {
        laserDrawX = playerDrawX + playerDraw.getBitmap().getWidth();
    }
    public void resetApple() { appleDrawY = appleDraw.getBitmap().getHeight() + 20;}
    public void resetLaser2() { laser2DrawX = enemyDrawX - laser2Draw.getBitmap().getWidth(); }
    public void resetBomb() { bombDrawY = bomb1Draw.getBitmap().getHeight() + 20; }

    public void enemyShoot() {
        laser2Visible = true;
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    protected void onDraw(Canvas c) {
        if(!beginFight)
        {
            playerDrawX = (this.getWidth()/4) - (playerDraw.getBitmap().getWidth()/2);
            playerDrawY = (this.getHeight()/2) - (playerDraw.getBitmap().getHeight()/2);
            enemyDrawX = ((this.getWidth()/4)*3) - (enemyDraw.getBitmap().getWidth()/2);
            enemyDrawY = (this.getHeight()/2) - (enemyDraw.getBitmap().getHeight()/2);

            playerExpDrawX = (this.getWidth()/4) - (explosionDraw.getBitmap().getWidth()/2);
            playerExpDrawY = (this.getHeight()/2) - (explosionDraw.getBitmap().getHeight()/2);
            enemyExpDrawX = ((this.getWidth()/4)*3) - (explosionDraw.getBitmap().getWidth()/2);
            enemyExpDrawY = (this.getHeight()/2) - (explosionDraw.getBitmap().getHeight()/2);

            shieldDrawX = playerDrawX + playerDraw.getBitmap().getWidth();
            shieldDrawY = (this.getHeight()/2) - (shieldDraw.getBitmap().getHeight()/2);
            laserDrawX = playerDrawX + playerDraw.getBitmap().getWidth();
            laserDrawY = playerDrawY + (playerDraw.getBitmap().getHeight()/3);
            appleDrawX = playerDrawX + (playerDraw.getBitmap().getWidth()/2) - (appleDraw.getBitmap().getWidth()/2);
            appleDrawY = appleDraw.getBitmap().getHeight() + 20;
            bombDrawX = enemyDrawX + (enemyDraw.getBitmap().getWidth()/2) - (bomb1Draw.getBitmap().getWidth()/2);
            bombDrawY = bomb1Draw.getBitmap().getHeight() + 20;

            laser2DrawX = enemyDrawX - laser2Draw.getBitmap().getWidth();
            laser2DrawY = enemyDrawY + (enemyDraw.getBitmap().getHeight()/3);

            playerExplode = false;
            enemyExplode = false;
            playerExplodeAnim = false;
            enemyExplodeAnim = false;

            beginFight = true;
        }

        if(laser2Visible) {
            c.drawBitmap(laser2Draw.getBitmap(), laser2DrawX, laser2DrawY, null);
            laser2DrawX -= laser2Speed;
            if(laser2DrawX <= (playerDrawX + playerDraw.getBitmap().getWidth())) {
                laser2Visible = false;
                resetLaser2();
                if(!shieldVisible) {
                    playerHP -=3;
                }
            }
        }else if(!laser2Visible && (enemyHP > 0)) {
            laser2Visible = true;
            playLaser();
        }
        if(laserVisible) {
            c.drawBitmap(laserDraw.getBitmap(), laserDrawX, laserDrawY, null);
            laserDrawX += laserSpeed;
            if(laserDrawX >= enemyDrawX) {
                laserVisible = false;
                resetLaser();
                enemyHP -= 2;
            }
        }
        if(appleVisible && (!appleCooldown)) {
            c.drawBitmap(appleDraw.getBitmap(), appleDrawX, appleDrawY, null);
            appleDrawY += appleSpeed;
            if(appleDrawY >= enemyDrawY) {
                resetApple();
                playerHP += 2;
                appleCooldown = true;
                startApple();
                playApple();
            }
        }
        if(shieldVisible) {
            c.drawBitmap(shieldDraw.getBitmap(), shieldDrawX, shieldDrawY, null);
        }



        //sound ifs
        if(laserPlay) {
            playLaser();
        }
        if(explosionPlay) {
            playExplosion();
        }
        if(shieldPlay) {
            playShield();
        }
        ///////////

        if(enemyHP > 0 && (!enemyExplode)) {
            c.drawRect(enemyDrawX, enemyDrawY-50, enemyDrawX+(enemyHP*6), enemyDrawY-40, paint);
            c.drawBitmap(enemyDraw.getBitmap(), enemyDrawX, enemyDrawY, null);
        }else if(enemyHP <= 0 && (!enemyExplode)) {
            playExplosion();
            enemyExplode = true;
            startEnemyExplode();
        }else if(enemyExplodeAnim) {
            c.drawBitmap(explosionDraw.getBitmap(), enemyExpDrawX, enemyExpDrawY, null);
        }

        if(playerHP > 0 && (!playerExplode)) {
            c.drawRect(playerDrawX, playerDrawY-50, playerDrawX+(playerHP*5), playerDrawY-40, paint);
            c.drawBitmap(playerDraw.getBitmap(), playerDrawX, playerDrawY, null);
        } else if(playerHP <= 0 && (!playerExplode)){
            playExplosion();
            playerExplode = true;
            startPlayerExplode();
        }else if(playerExplodeAnim) {
            c.drawBitmap(explosionDraw.getBitmap(), playerExpDrawX, playerExpDrawY, null);
        }

        if(bombVisible && (!bombAnimStart)) {
            c.drawBitmap(bomb1Draw.getBitmap(), bombDrawX, bombDrawY, null);
            bombDrawY += bombSpeed;
            if(bombDrawY >= enemyDrawY) {
                bombAnimStart = true;
                startBomb();
                bomb1Anim = true;
            }
        }else if(bombAnimStart && bomb1Anim) {
            c.drawBitmap(bomb1Draw.getBitmap(), bombDrawX, bombDrawY, null);
        }else if(bombAnimStart && bomb2Anim) {
            c.drawBitmap(bomb2Draw.getBitmap(), bombDrawX, bombDrawY, null);
        }

        if(bombExplode) {
            c.drawBitmap(boomDraw.getBitmap(), bombDrawX-50, bombDrawY-20, null);
        }

        h.postDelayed(r, FRAME_RATE);
    }
}
