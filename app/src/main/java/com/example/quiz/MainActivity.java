package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button start;
    Game game;
    Timer timer;
    TextView time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = (TextView) findViewById(R.id.time);

        game = new Game();
        timer = new Timer();
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.start();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        time.setText(game.getTimeString());
                    }
                }, 1, 250);
            }
        });
    }
}
class Game {
    private long startTime;
    public void start() {
        this.startTime = System.currentTimeMillis();
    }
    public long getRemainingTime() {
        return startTime + 60000 - System.currentTimeMillis();
    }
    public String getTimeString() {
        long remainingSeconds = getRemainingTime() / 1000;
        return (remainingSeconds < 10 ? "0:0" : "0:") + remainingSeconds;
    }
}