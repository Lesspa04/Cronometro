package co.edu.unipiloto.cronometro;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private int seconds = 0;
    private boolean running;
    private boolean wasRunning;

    private ArrayList<Integer> laps = new ArrayList<>();
    private static final int MAX_LAPS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runTimer();

        Button start = findViewById(R.id.start_button);
        Button stop = findViewById(R.id.stop_button);
        Button reset = findViewById(R.id.reset_button);
        Button lap = findViewById(R.id.lap_button);

        start.setOnClickListener(v -> running = true);
        stop.setOnClickListener(v -> running = false);

        reset.setOnClickListener(v -> {
            running = false;
            seconds = 0;
            laps.clear();
            ((TextView)findViewById(R.id.lap_times)).setText("Vueltas:\n");
        });

        lap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running && laps.size() < MAX_LAPS){
                    laps.add(seconds);
                    mostrarVueltas();
                    if(laps.size() == MAX_LAPS){
                        running = false;
                    }
                }
            }
        });

        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
            laps = (ArrayList<Integer>) savedInstanceState.getSerializable("laps");
            mostrarVueltas();
        }
    }

    private void runTimer() {
        final TextView timeView = findViewById(R.id.time_view);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(),
                        "%02d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);

                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void mostrarVueltas() {
        TextView lapView = findViewById(R.id.lap_times);
        StringBuilder sb = new StringBuilder("Vueltas:\n");
        for(int i=0; i<laps.size(); i++){
            int lapTime;
            if(i == 0){
                lapTime = laps.get(i);
            } else {
                lapTime = laps.get(i) - laps.get(i-1);
            }
            int mins = (lapTime % 3600) / 60;
            int secs = lapTime % 60;
            sb.append("Vuelta ").append(i+1).append(": ")
                    .append(String.format(Locale.getDefault(), "%02d:%02d", mins, secs))
                    .append("\n");
        }
        lapView.setText(sb.toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);
        outState.putSerializable("laps", laps);
    }
}
