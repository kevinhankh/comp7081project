package kevinhan.captainhypothesis;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent playIntent = getIntent();

        setContentView(R.layout.activity_play);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void abilityDefensive(View view) {
        PlayView.activateShield();
    }
    public void abilityOne(View view) {
        PlayView.shootLaser();
    }
    public void abilityTwo(View view) {
        PlayView.shootApple();
    }
    public void abilityAttack(View view) {
        PlayView.throwBomb();
    }
}
