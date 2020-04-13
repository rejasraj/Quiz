package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Button start;
    Game game;
    Timer timer;
    Handler handler;
    TextView time;
    EditText answer;
    TextView problem;
    TextView score;
    TextView highScore;
    Gson gson;
    SavedState savedState;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        gson = new Gson();
        String json = sharedPreferences.getString("savedState", "");
        savedState = json.isEmpty() ? new SavedState(0) : gson.fromJson(json, SavedState.class);

        highScore = (TextView) findViewById(R.id.highScore);
        highScore.setText("High Score: " + savedState.getHighScore());
        time = (TextView) findViewById(R.id.time);
        answer = (EditText) findViewById(R.id.answer);
        problem = (TextView) findViewById(R.id.problem);
        score = (TextView) findViewById(R.id.score);
        game = new Game();
        timer = new Timer();
        handler = new Handler();
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.start();
                problem.setText(game.getA() + " * " + game.getB() + " = ");
                answer.getText().clear();
                score.setText("Score: 0");
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!game.isRunning()) {
                                    cancel();
                                }
                                else
                                    time.setText(game.getTimeString());
                            }
                        });
                    }
                }, 1, 250);
            }
        });
        answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty())
                    return;
                if (!game.isRunning())
                    return;
                int response = Integer.parseInt(s.toString());
                if (game.isCorrect(response)) {
                    game.setScore(game.getScore() + 1);
                    score.setText("Score: " + game.getScore());
                    game.generateProblem();
                    problem.setText(game.getA() + " * " + game.getB() + " = ");
                    answer.getText().clear();
                    if (game.getScore() > savedState.getHighScore()) {
                        savedState.setHighScore(game.getScore());
                        highScore.setText("High Score: " + savedState.getHighScore());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedState", gson.toJson(savedState));
        editor.apply();
    }
}
class Game {
    private long startTime;
    private int a;
    private int b;
    private int c;
    private int score;
    public Game() {
        startTime = 0;
    }
    public void start() {
        generateProblem();
        this.score = 0;
        this.startTime = System.currentTimeMillis();
    }
    public long getRemainingTime() {
        return startTime + 60000 - System.currentTimeMillis();
    }
    public String getTimeString() {
        long remainingSeconds = getRemainingTime() / 1000;
        return (remainingSeconds < 10 ? "0:0" : "0:") + remainingSeconds;
    }
    public void generateProblem() {
        a = (int)(Math.random() * 90 + 10);
        b = (int)(Math.random() * 90 + 10);
        c = a * b;
    }
    public int getA() {
        return this.a;
    }
    public int getB() {
        return this.b;
    }
    public boolean isCorrect(int response) {
        return response == this.c;
    }
    public int getScore() {
        return this.score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public boolean isRunning() {
        return startTime != 0 && this.getRemainingTime() >= 0;
    }
}
class SavedState
{
    private int highScore;

    public SavedState(int highScore)
    {
        this.highScore = highScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
}