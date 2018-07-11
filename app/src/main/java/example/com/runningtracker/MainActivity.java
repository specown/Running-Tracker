package example.com.runningtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import example.com.runningtracker.entities.RunEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button startBtn;
    Button historyBtn;

    TextView distanceTodayTxt;
    TextView distanceMonthTxt;

    //Initialize Database
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        //This text is being used to display Today's / Month's results
        distanceTodayTxt = (TextView) findViewById(R.id.distanceTodayTxt);
        distanceMonthTxt = (TextView) findViewById(R.id.distanceMonthTxt);

        //Button to start running
        startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(this);

        //Button to check history
        historyBtn = (Button) findViewById(R.id.historyBtn);
        historyBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        distanceTodayTxt.setText(getDistanceToday());
        distanceMonthTxt.setText(getDistanceMonth());

    }

    
    //Gets total distance today
    private String getDistanceToday()
    {
        //Get run data from database
        ArrayList<RunEntry> runs = db.getAllData();
        //Date format (with day)
        DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
        double distance = 0;

        //Check if any runs
        if(!runs.isEmpty())
            for(RunEntry run : runs)
            {
                //If today's date matches today - add to distance variable
                if(outputFormatter.format(run.getDate()).equals(outputFormatter.format(new Date()))) {
                    distance += run.getDistance();
                }
            }
        return "TOTAL DISTANCE TODAY: " + String.format("%.2f", distance) + " KM";
    }

    //Gets total distance this month
    private String getDistanceMonth()
    {
        //Retrieves run data from DB
        ArrayList<RunEntry> runs = db.getAllData();
        //Date format (only month)
        DateFormat outputFormatter = new SimpleDateFormat("MM/yyyy");
        double distance = 0;

        //Check if any runs
        if(!runs.isEmpty())
            for(RunEntry run : runs) {
                //If month is equal to this month - add to variable
                if(outputFormatter.format(run.getDate()).equals(outputFormatter.format(new Date()))) {
                    distance += run.getDistance();
                }
            }
        return "TOTAL DISTANCE THIS MONTH: " + String.format("%.2f", distance) + " KM";
    }

    public void onClick(View v) {

        switch (v.getId())
        {
            //If start button pressed
            case R.id.startBtn:
                //Check if location permission is enabled
                if(checkLocationPermission()) {
                    Intent intent = new Intent(MainActivity.this, RunningActivity.class);
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
                break;

            //If run history button is pressed
            case R.id.historyBtn:
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                break;

            //If nothing is pressed - do nothing
            default:
                break;
        }

    }

    //Checks whether location permission is enabled for tracking
    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                //If request is cancelled - the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, RunningActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            //Other case lines can be added to check other permissions if needed

        }
    }

}
