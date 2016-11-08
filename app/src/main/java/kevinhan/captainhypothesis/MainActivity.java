package kevinhan.captainhypothesis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playButton(View view) {
        Intent levelSelectIntent = new Intent(this, LevelSelectActivity.class);
        startActivity(levelSelectIntent);
    }

    public void settingsButton(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void exitButton(View view) {
        System.exit(0);
    }
}
