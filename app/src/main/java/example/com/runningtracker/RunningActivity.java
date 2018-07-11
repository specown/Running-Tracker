package example.com.runningtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import example.com.runningtracker.entities.RunEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RunningActivity extends AppCompatActivity implements View.OnClickListener {

    Button stopBtn;
    TextView distanceTxt;
    TextView timeTxt;

    Intent RunningService;

    long duration;
    double distance;

    boolean stopped = false;

    DatabaseHelper db;
    Handler handler;
    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        db = new DatabaseHelper(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        distanceTxt = (TextView) findViewById(R.id.DistanceTxt);
        timeTxt = (TextView) findViewById(R.id.TimeTxt);

        stopBtn = (Button) findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(this);

        //Start running service
        RunningService = new Intent(RunningActivity.this, RunningService.class);
        startService(RunningService);

        //Check if intent matches intent filter
        IntentFilter filter = new IntentFilter("RUNNING_UPDATE");
        registerReceiver(RunningReceiver, filter);

        //For keeping track of running time
        handler = new Handler();
        startTime = System.currentTimeMillis();
        getDuration();
    }

    //Initialize broadcast
    private final BroadcastReceiver RunningReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            distance = intent.getDoubleExtra("distance", 0)/1000;
    //      duration = intent.getLongExtra("time", 0);

            distanceTxt.setText("Distance: " + String.format("%.2f", distance) + " km");
        }
    };

    //Gets duration of the run
    public void getDuration() {
        duration = System.currentTimeMillis() - startTime;

        //Reformat the running time
        timeTxt.setText("Time elapsed: " + String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))));

        //Recall the method with delay
        Runnable runnable = new Runnable() {
            @Override
            public void run() {getDuration();
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopService(RunningService);
        unregisterReceiver(RunningReceiver);
    }

    @Override
    //Check if back arrow is pressed
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Check if the current run is the best run today
    public boolean isBest()
    {
        //Get data from DataBase
        ArrayList<RunEntry> runs = db.getAllData();
        //Date format
        DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
        //Run ratio
        double cRatio = distance /(double) duration;

        //Check if any runs
        if(!runs.isEmpty())
        for(RunEntry run : runs)
        {
            //If date matches today's date
            if(outputFormatter.format(run.getDate()).equals(outputFormatter.format(new Date())))

            {
                //Compare distance with ratio
                double ratio = run.getDistance() /(double) run.getDuration();
                if (ratio >= cRatio) return false;
            }
        }
        Toast.makeText(this, "You've ran the fastest today!", Toast.LENGTH_LONG).show();
        return true;
    }

    public void onClick(View v) {

        switch (v.getId()) {

            //If stop button is pressed
            case R.id.stopBtn:
                if(!stopped) {
                    isBest();
                    if (distance != -1 && duration != -1)
                        //Add's new entry to the list
                        db.addRun(new RunEntry(duration, distance, new Date()));
                    stopService(RunningService);
                    stopped = true;
                    handler.removeCallbacksAndMessages(null);
                    stopBtn.setText("Go back");
                } else {
                    finish();
                }
                break;

            default:
                break;
        }

    }
}
